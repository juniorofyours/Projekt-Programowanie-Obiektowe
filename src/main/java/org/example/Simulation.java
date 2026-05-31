package org.example;

import java.util.ArrayList;

public class Simulation{
    private Board board;
    private GarlicContainer container;
    private ArrayList<Agent> agents;
    private ArrayList<Human> newPeople;
    private ArrayList<Garlic> garlics;
    private int numSteps;
    private int hour;
    private Boolean isCycling;

    public Simulation(int width, int height){
        board=new Board(width, height);
        agents=new ArrayList<>();
        newPeople=new ArrayList<>();
        garlics=new ArrayList<>();
        numSteps=0;
        hour=0;
        container=null;
    }

    public void addAgent(Agent agent){ //dodaje agenta na planszę i do listy agents w symulacji
        board.addToBoard(agent);
        agents.add(agent);
    }
    public void removeAgent(Agent agent){ //usuwa agenta z planszy i z listy agents w symulacji
        board.removeFromBoard(agent);
        agents.remove(agent);
    }

    public void addNewHuman(Human human){
        newPeople.add(human);
    } //dodaje nowego człowieka do listy newPeople,
//    konkretnie gdy ludzie rodzą nowych ludzi (przez to, że iterujemy po wszystkich ludziach i ich metodzie tryAdd,
//    mogą oni dodać nowych ludzi, ale jeśli by ich dodali od razu do listy agents, wystąpiłyby problemy z iteracją
//    po ludziach, bo by dynamicznie pojawiły się nowe elementy właśnie do tej listy po której iterujemy. Przez to
//    najpierw dodajemy nowych ludzi do listy newPeople, a później opróżnaimy tę listę i dodajemy tych ludzi do agents
//    za pomocą metody addNewPeopleToAgents

    public void addGarlic(Garlic garlic){ //dodaje nowy czosnek na planszę i do listy garlics
        board.addGarlicToBoard(garlic);
        garlics.add(garlic);
    }
    public void removeGarlic(Garlic garlic){ //usuwa czosnek z planszy i z listy garlics
        board.removeGarlicFromBoard(garlic);
        garlics.remove(garlic);
    }

    private void addNewPeopleToAgents(){ //dodaje wszystkie elementy z listy newPeople na planszę i do listy agents,
//        oraz opróżnia listę newPeople
        for(Human human : newPeople){
            addAgent(human);
        }
        newPeople.clear();
    }

    private void tryAddNewPeople(){ //iteruje po wszystkich ludziach i wywołuje na nich metodę tryAdd, która
//        dodaje z pewnym prawdopodobieństwem nowego człowieka do listy newHuman. Po skończeniu pętli wszystkie
//        nowe osoby są dodawane do planszy i listy agents za pomocą addNewPeopleToAgents()
        for(Agent agent :agents){
            if(agent instanceof Human){
                ((Human) agent).tryAdd();
            }
        }
        addNewPeopleToAgents();
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
        for(Agent agent : agents){
            agent.interact();
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
        if(isCycling==true) if(numSteps%50==0) {
            hour=(hour+1)%24;
        }
    }

    private void step(){ //metoda oznaczająca jeden krok symulacji i wywołująca wszystkie meotdy
//        iterujące po agentach i zmieniające ich stan, położenie, przeprowadzjące ich interkację, śmerć, ...
        updateAgentStates();
        tryAddNewPeople();
        moveAgents();
        conductAgentInteractions();
        tryRemoveAgents();
        numSteps++;
        updateHour();

    }
}
