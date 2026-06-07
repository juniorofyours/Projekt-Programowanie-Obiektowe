package org.example;

public class AgentCreator {
    Simulation simulation;
    Board board;
    SimulationConfig config=SimulationConfig.getInstance();
    SimulationStats stats=SimulationStats.getInstance();
    public AgentCreator(Simulation simulation, Board board){
        this.simulation=simulation;
        this.board=board;
    }
    public void createVampires(int number){
        for(int i=0;i<number;i++){
            Agent vampire=new Vampire(simulation, board, 0, 0, config.getVampireConfig().getEnergyBoost(),config.getVampireConfig().getEnergyLoss());
            vampire.randomizePosition();
            simulation.addAgent(vampire);
        }
    }
    public void createPeople(int number){
        for(int i=0;i<number;i++){
            Agent human=new Human(simulation, board, 0, 0, config.getHumanConfig().getTransformationProb(), config.getHumanConfig().getAddProb(),
                    config.getHumanConfig().getEnergyBoost(), config.getHumanConfig().getEnergyLoss());
            human.randomizePosition();
            simulation.addAgent(human);
        }
    }
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
