package org.example;

import java.util.ArrayList;

public class Simulation implements Runnable{
    private SimulationConfig config=SimulationConfig.getInstance();
    private Board board;
    private GarlicContainer container;
    private ArrayList<Agent> agents;
    private ArrayList<Garlic> garlics;
    private AgentCreator creator;
    private int numSteps;
    private int hour;
//    private Boolean isCycling;
    private int sunsetHour;
    private int sunriseHour;

    public Simulation(){
//        board=new Board(width, height);
        agents=new ArrayList<>();
        garlics=new ArrayList<>();
        numSteps=0;
        hour=0;
        container=null;
        sunriseHour=6;
        sunsetHour=18;
    }

    private void init(){
//        parametry
        int width=config.getWorldConfig().getWidth();
        int height=config.getWorldConfig().getHeight();
        board=new Board(width, height);

        int container_width=width/5;
        int container_height=height/5;

        int min_x=width-1-container_width;
        int max_x=width-1;
        int min_y=0;
        int max_y=container_height;
        container=new GarlicContainer(min_x, max_x, min_y,max_y);
        board.addGarlicContainer(container);

        creator=new AgentCreator(this, board);
        creator.createVampires(config.getVampireConfig().getInitialNumber());
        creator.createPeople(config.getHumanConfig().getInitialNumber());
        creator.createTrainedPeople(config.getTrainedHumanConfig().getInitialNumber());

        config.getWorldConfig().setInitiated(true);

//        int energy_boost=1000;
//        int energy_loss=1000;
//
//        float transformation_prob=5f;
//        float add_prob=1f;
//        float recruitment_prob=10f;
//        float throw_prob=10f;
//        addAgent(new Vampire(this, board, 5, 10, energy_boost, energy_loss));
//        addAgent(new Human(this, board, 5, 10,transformation_prob,add_prob, energy_boost, energy_loss));
//        addAgent(new TrainedHuman(this, board, 5, 10,transformation_prob,add_prob,recruitment_prob, throw_prob, energy_boost, energy_loss));
    }

    @Override
public void run(){
        init();
        while(true){
            if(!SimulationConfig.getInstance().getWorldConfig().isPaused()){
                step();
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addAgent(Agent agent){ //dodaje agenta na planszę i do listy agents w symulacji
        board.addToBoard(agent);
        agents.add(agent);
    }
    public void removeAgent(Agent agent){ //usuwa agenta z planszy i z listy agents w symulacji
        board.removeFromBoard(agent);
        agents.remove(agent);
    }

    public void replaceAgent(Agent agentToBeReplaced, Agent newAgent){ //metoda, która zmienia już istniejący element
//        listy na nowy element, przydatna przy zamianie człowieka w wampira, lub człowieka w wytrenowanego człowieka, żeby
//        uniknąć problemów z iteracją po agentach w liście, metoda ta wywołuje również board.replaceAgentInCell, która robi to samo,
//        tylko, że z listami w komórkach (Cell)
        int index=agents.indexOf(agentToBeReplaced);
        agents.set(index, newAgent);

        board.replaceAgentInCell(agentToBeReplaced, newAgent);
    }

    public void addGarlic(Garlic garlic){ //dodaje nowy czosnek na planszę i do listy garlics
        board.addGarlicToBoard(garlic);
        garlics.add(garlic);
    }
    public void removeGarlic(Garlic garlic){ //usuwa czosnek z planszy i z listy garlics
        board.removeGarlicFromBoard(garlic);
        garlics.remove(garlic);
    }

    private void tryAddNewPeople(){ //iteruje po wszystkich ludziach i wywołuje na nich metodę tryAdd
        for(int i=0; i<agents.size();i++){
            if(agents.get(i) instanceof Human){
                ((Human) agents.get(i)).tryAdd();
            }
        }
    }

    private void updateAgentStates(){ //iteruje po wszystkich agentach i aktualizuje ich stan( dla człowieka
//        sprawdza czy dalej jest chroniony przed atakiem przez zjedzenie czosnku, dla wytrenowanego człowieka
//        sprawdza to samo + czy będzie mógł rozrzucać i jeść czosnek w zależności od tego ile mineło kroków
//        od poprzedniego rozrzucenia czosnku oraz aktualizuje jego strategię poruszania się w zależności od tego
//        czy nadal posiada czosnek czy już nie. Dla wampirów updateCurrentState sprawdza, która jest godzina
//        i jeśli 6:00 to się chowa, a 18:00 pojawia się z powrotem na planszy
        for(Agent agent : agents){
            agent.updateCurrentState();
        }
    }
    private void moveAgents(){ //iteruje po wszystkich agentach i wywołuje ich metodę do poruszania się
        for(Agent agent : agents){
            agent.move();
        }
    }
    private void conductAgentInteractions(){ //iteruje po wszystkich agentach i wywołuje ich metodę interakcji
//        z otoczeniem, implementującą ich wszystkie zachowania typu zjedzenie cozsnku, zaatakowanie, rozrzucenie
//        czosnku itd
//        System.out.println("Interakcje agentów");
        for(int i=0;i<agents.size();i++){
            agents.get(i).interact();
        }
    }
    private void tryRemoveAgents(){ //iteruje po wszystkich agentach i ich metodzie sprawdzającej czy powinni umrzeć
//        jeśli tak, metoda danego agenta tryRemove() usunie tego agenta przy pomocy metody symulacji removeAgent()
        for(int i=0;i<agents.size();i++){
            if(agents.get(i).tryRemove()) i--;
        }
    }
    private void updateHour(){ //aktualizuje godzinę symulacji, jeśli występuje tryb dzień-noc.
//        Tutaj jest założone, że godzina się zmienia zawsze, gdy numer kroku dzieli się przez 50, czyli
//        co 50 kroków
        if(config.getWorldConfig().isCycling()==true) if(numSteps%50==0) {
            hour=(hour+1)%24;
        }
    }

    //zwraca aktualną liczbę kroków symulacji
    public int getCurrentStep(){
        return this.numSteps;
    }
    public int getCurrentHour(){
        return hour;
    }

    public int getSunriseHour(){
        return sunriseHour;
    }
    public int getSunsetHour(){
        return sunsetHour;
    }

    public synchronized void step(){ //metoda oznaczająca jeden krok symulacji i wywołująca wszystkie meotdy
//        iterujące po agentach i zmieniające ich stan, położenie, przeprowadzjące ich interkację, śmerć, ...
        ConsoleColors.printlnBlue("\nKrok: "+numSteps+ ", godzina: "+hour);
        updateAgentStates();
        tryAddNewPeople();
        moveAgents();
        conductAgentInteractions();
        tryRemoveAgents();
        numSteps++;
        updateHour();

    }

    public synchronized ArrayList<ObjectToRender> getSnapshot(){
        if(!config.getWorldConfig().isInitiated()) return null;
        ArrayList<ObjectToRender> listToRender=new ArrayList<>();
        for(int i=0;i< board.getWidth();i++){
            for(int j=0;j< board.getHeight();j++){
                Object obj=board.getCell(i, j).getFirstObject();
                    if(obj==null) continue;
                    String type=recognizeType(obj);
                    listToRender.add(new ObjectToRender(i,j,type));
            }
        }
        return listToRender;
    }

    public String recognizeType(Object obj){
        if(obj instanceof Vampire){
            return "VAMPIRE";
        }
        if(obj instanceof TrainedHuman){
            return "TRAINED_HUMAN";
        }
        if(obj instanceof Human){
            return "HUMAN";
        }
        if(obj instanceof Garlic){
            return "GARLIC";
        }
        if(obj instanceof GarlicContainerCell){
            return "GARLIC_CONTAINER_CELL";
        }
        return null;
    }

    //zwraca liczbe osob
    public long getNumberOfHumanBeings(){
        long number = agents.stream().filter(agent -> agent instanceof Human).count();
        return number;
    }

    //zwraca liczbe wampirów
    public long getNumberOfVampires(){
        long number = agents.stream().filter(agent -> agent instanceof Vampire).count();
        return number;
    }

    public int[] getCoordinatesOfContainer() {
        int[] coords = new int[4];
        coords[0] = container.getX_min();
        coords[1] = container.getX_max();
        coords[2] = container.getY_min();
        coords[3] = container.getY_max();

        return coords;
    }
}
