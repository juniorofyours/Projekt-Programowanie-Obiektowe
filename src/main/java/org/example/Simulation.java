package org.example;

import java.util.ArrayList;
import java.util.List;


/**
 * Główny silnik symulacji agentowej.
 * <p>
 * Klasa implementuje interfejs {@link Runnable}, co pozwala na uruchomienie symulacji
 * w osobnym wątku. Odpowiada za inicjalizację świata, zarządzanie czasem (taktami zegara)
 * oraz pełnym cyklem życia agentów (ludzi, trenowanych ludzi, wampirów) i obiektów (czosnek).
 * </p>
 */
public class Simulation implements Runnable{
    private final SimulationConfig config;
    private final SimulationStats stats;
    private final SimulationClock clock;
    private Board board;
    private GarlicContainer container;
    private final List<Agent> agents;
    private final List<Garlic> garlics;
    private AgentCreator creator;

    /**
     * Konstruuje nową instancję symulacji.
     * Inicjalizuje listy agentów i czosnku oraz pobiera obiekty konfiguracji, statystyk i zegara
     */
    public Simulation(){
        agents=new ArrayList<>();
        garlics=new ArrayList<>();
        config=SimulationConfig.getInstance();
        stats=SimulationStats.getInstance();
        clock=SimulationClock.getInstance();
    }

    /**
     * Inicjalizuje stan początkowy symulacji.
     * <p>
     * Metoda tworzy planszę, kontener na czosnek
     * oraz zleca obiektowi {@link AgentCreator} stworzenie początkowej liczby wampirów,
     * zwykłych ludzi i wytrenowanych ludzi
     * </p>
     */
    public void init(){
//        parametry
        int width=config.getWorldConfig().getWidth();
        int height=config.getWorldConfig().getHeight();
        board=new Board(width, height);
        creator=new AgentCreator(this, board);

        int container_width=width/5;
        int container_height=height/5;

        int min_x=width-1-container_width;
        int max_x=width-1;
        int min_y=0;
        int max_y=container_height;
        container=new GarlicContainer(min_x, max_x, min_y,max_y);
        board.addGarlicContainer(container);

        creator.createVampires(config.getVampireConfig().getInitialNumber());
        creator.createPeople(config.getHumanConfig().getInitialNumber());
        creator.createTrainedPeople(config.getTrainedHumanConfig().getInitialNumber());

        config.getWorldConfig().setInitiated(true);
    }

    /**
     * Główna pętla symulacji.
     * Odpowiada za cykliczne wywoływanie kroków symulacji, gdy symulacja
     * nie jest zapauzowana
     */
    @Override
    public void run(){
        init();
        while(true){
            if(!SimulationConfig.getInstance().getWorldConfig().isPaused()){
                step();
            }

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Dodaje nowego agenta do symulacji (dodaje go na planszę i do
     * wewnętrznej listy symulacji oraz aktualizuje globalne statystyki).
     * * @param agent Obiekt nowego agenta.
     */
    public void addAgent(Agent agent){
        board.addToBoard(agent);
        agents.add(agent);

        stats.addObjectOfType(this.recognizeType(agent));
    }

    /**
     * Usuwa wybranego agenta z symulacji.
     * Usuwa go z planszy, listy agentów oraz aktualizuje statystyki.
     * * @param agent Agent do usunięcia.
     */
    public void removeAgent(Agent agent){
        board.removeFromBoard(agent);
        agents.remove(agent);

        stats.removeObjectOfType(this.recognizeType(agent));
    }

    /**
     * Dokonuje bezpiecznej zmiany jednego agenta na drugiego w trakcie iteracji.
     * <p>
     * Jest przydatna w trakcie transformacji agentów, takich jak przemiana człowieka
     * w wampira lub rekrutacja człowieka na człowieka wyszkolonego. Metoda podmienia agenta na planszy,
     * oraz liście agentów i modyfikuje statystyki agentów.
     * </p>
     * * @param agentToBeReplaced Dotychczasowy agent, który ma zostać usunięty.
     * @param newAgent Nowy agent, który ma być obsadzony w tym samym miejscu, co poprzednik
     */
    public void replaceAgent(Agent agentToBeReplaced, Agent newAgent){
        int index=agents.indexOf(agentToBeReplaced);
        agents.set(index, newAgent);

        board.replaceAgentInCell(agentToBeReplaced, newAgent);

        stats.addObjectOfType(this.recognizeType(newAgent));
        stats.removeObjectOfType(this.recognizeType(agentToBeReplaced));
    }

    /**
     * Umieszcza nowy czosnek w symulacji i aktualizuje statystykę liczby czosnku
     * * @param garlic Nowy czosnek do umieszczenia w symulacji
     */
    public void addGarlic(Garlic garlic){
        board.addGarlicToBoard(garlic);
        garlics.add(garlic);

        stats.addObjectOfType(ObjectType.GARLIC);
    }

    /**
     * Usuwa zjedzony czosnek z planszy i listy czosnku symulacji.
     * * @param garlic Czosnek do usunięcia.
     */
    public void removeGarlic(Garlic garlic){
        board.removeGarlicFromBoard(garlic);
        garlics.remove(garlic);

        stats.removeObjectOfType(ObjectType.GARLIC);
    }

    /**
     * Wywołuje dla każdego człowieka w symulacji metodę próby dodania nowego człowieka.
     */
    private void tryAddNewPeople(){
        for(int i=0; i<agents.size();i++){
            if(agents.get(i) instanceof Human){
                ((Human) agents.get(i)).tryAdd();
            }
        }
    }

    /**
     * Aktualizuje stany wewnętrzne wszystkich agentów.
     * <p>
     * Obejmuje m.in. licznik kroków do ponownego użycia czosnku
     * czy zmianę stanu wampirów ze względu na godzinę symulacji
     * </p>
     */
    private void updateAgentStates(){
        for(Agent agent : agents){
            agent.updateCurrentState();
        }
    }

    /**
     * Wywołuje metodę ruchu dla wszystkich agentów.
     */
    private void moveAgents(){
        for(Agent agent : agents){
            agent.move();
        }
    }

    /**
     * Przeprowadza fazę interakcji między wszystkimi agentami w symulacji.
     */
    private void conductAgentInteractions(){
        for(int i=0;i<agents.size();i++){
            agents.get(i).interact();
        }
    }

    /**
     * Weryfikuje warunki śmierci dla wszystkich agentów.
     * Jeśli warunek śmierci jest spełniony, agent jest usuwany z symulacji
     */
    private void tryRemoveAgents(){
        for(int i=0;i<agents.size();i++){
            if(agents.get(i).tryRemove()) i--;
        }
    }


    /**
     * Zwraca całkowitą liczbę ludzi w symulacji.
     * * @return Liczba obiektów typu {@link Human}.
     */
    public long getNumberOfHumanBeings(){
        long number = agents.stream().filter(agent -> agent instanceof Human).count();
        return number;
    }

    /**
     * Zwraca całkowitą liczbę wyszkolonych ludzi w symulacji.
     * * @return Liczba obiektów typu {@link TrainedHuman}.
     */
    //zwraca liczbe wyszkolonych osób
    public long getNumberOfTrainedHumanBeings(){
        long number = agents.stream().filter(agent -> agent instanceof TrainedHuman).count();
        return number;
    }

    /**
     * Zwraca całkowitą liczbę wampirów w symulacji.
     * * @return Liczba obiektów typu {@link Vampire}.
     */
    //zwraca liczbe wampirów
    public long getNumberOfVampires(){
        long number = agents.stream().filter(agent -> agent instanceof Vampire).count();
        return number;
    }

    /**
     * Pobiera granice kontenera na czosnek.
     * * @return Tablica czteroelementowa zawierająca współrzędne: [X_min, X_max, Y_min, Y_max].
     */
    public int[] getCoordinatesOfContainer() {
        int[] coords = new int[4];
        coords[0] = container.getX_min();
        coords[1] = container.getX_max();
        coords[2] = container.getY_min();
        coords[3] = container.getY_max();

        return coords;
    }

    /**
     * Wykonuje pojedynczy, zsynchronizowany krok symulacji.
     * <p>
     * Metoda zawiera następujące po sobie fazy aktualizacji stanu agentów,
     * dodawania nowych ludzi, ruchu, interakcji, usuwania agentów i aktualizacji stanu zegara symulacji
     *
     * </p>
     */
    public synchronized void step(){
        ConsoleColors.printlnBlue("\nKrok: "+clock.getStep()+ ", godzina: "+Math.floor(clock.getHour()));
        updateAgentStates();
        tryAddNewPeople();
        moveAgents();
        conductAgentInteractions();
        tryRemoveAgents();
        clock.updateClock();

    }

    /**
     * Generuje aktualny snapshot planszy przeznaczony do późniejszego renderowania planszy w GUI.
     * Mapuje obiekty znajdujące się w komórkach planszy na lekkie obiekty zawierające tylko dane potrzebne do
     * prawidłowego wyrenderowania planszy
     * * @return Lista obiektów do wyświetlenia lub {@code null}, jeśli świat nie został jeszcze zainicjalizowany.
     */
    public synchronized ArrayList<ObjectToRender> getSnapshot(){
        if(!config.getWorldConfig().isInitiated()) return null;
        ArrayList<ObjectToRender> listToRender=new ArrayList<>();
        for(int i=0;i< board.getWidth();i++){
            for(int j=0;j< board.getHeight();j++){
                Object obj=board.getCell(i, j).getFirstObject();
                    if(obj==null) continue;
                    ObjectType type=recognizeType(obj);
                    listToRender.add(new ObjectToRender(i,j,type));
            }
        }
        return listToRender;
    }

    /**
     * Identyfikuje i mapuje dany obiekt na obiekt typu {@link ObjectType} zawierający informację
     * o typie obiektu
     * * @param obj Dowolny obiekt obecny w symulacji.
     * @return Odpowiadający obiektowi {@link ObjectType} lub {@code null}, jeśli obiekt nie został poprawnie zidentyfikowany
     */
    public ObjectType recognizeType(Object obj){
        if(obj instanceof Vampire){
            return ObjectType.VAMPIRE;
        }
        if(obj instanceof TrainedHuman){
            return ObjectType.TRAINED_HUMAN;
        }
        if(obj instanceof Human){
            return ObjectType.HUMAN;
        }
        if(obj instanceof Garlic){
            return ObjectType.GARLIC;
        }
        if(obj instanceof GarlicContainerCell){
            return ObjectType.GARLIC_CONTAINER_CELL;
        }
        return null;
    }


}
