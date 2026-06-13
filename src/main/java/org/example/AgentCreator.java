package org.example;

/**
 * Klasa odpowiedzialna za tworzenie agentów i dawanie im losowych współrzędnych.
 */
public class AgentCreator {
    private final Simulation simulation;
    private final Board board;
    private final SimulationConfig config=SimulationConfig.getInstance();

    /**
     * Konstruuje nową instancję kreatora agentów, i wiąże go z symulacją oraz planszą.
     * * @param simulation Instancja symulacji, do której będą dodawani agenci.
     * @param board      Plansza, na której agenci będą fizycznie rozmieszczani.
     */
    public AgentCreator(Simulation simulation, Board board){
        this.simulation=simulation;
        this.board=board;
    }

    /**
     * Tworzy określoną liczbę agentów typu {@link Vampire}.
     * * @param number Liczba wampirów do wygenerowania.
     */
    public void createVampires(int number){
        for(int i=0;i<number;i++){
            Agent vampire=new Vampire(simulation, board, 0, 0, config.getVampireConfig().getEnergyBoost(),config.getVampireConfig().getEnergyLoss());
            vampire.randomizePosition();
            simulation.addAgent(vampire);
        }
    }

    /**
     * Tworzy określoną liczbę agentów typu {@link Human}.
     * * @param number Liczba zwykłych ludzi do wygenerowania.
     */
    public void createPeople(int number){
        for(int i=0;i<number;i++){
            Agent human=new Human(simulation, board, 0, 0, config.getHumanConfig().getTransformationProb(), config.getHumanConfig().getAddProb(),
                    config.getHumanConfig().getEnergyBoost(), config.getHumanConfig().getEnergyLoss());
            human.randomizePosition();
            simulation.addAgent(human);
        }
    }

    /**
     * Tworzy określoną liczbę agentów typu {@link TrainedHuman}.
     * * @param number Liczba wytrenowanych ludzi do wygenerowania.
     */
    public void createTrainedPeople(int number){
        for(int i=0;i<number;i++){
            Agent trainedHuman=new TrainedHuman(simulation, board, 0, 0, config.getHumanConfig().getTransformationProb(), config.getHumanConfig().getAddProb(),
                    config.getTrainedHumanConfig().getRecruitmentProb(),config.getTrainedHumanConfig().getThrowProb(),config.getTrainedHumanConfig().getGarlicStockMax(),
                    config.getHumanConfig().getEnergyBoost(), config.getHumanConfig().getEnergyLoss());
            trainedHuman.randomizePosition();
            simulation.addAgent(trainedHuman);
        }
    }
}
