package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SimulationConfig {

    private final WorldConfig worldConfig =new WorldConfig();
    private final VampireConfig vampireConfig=new VampireConfig();
    private final HumanConfig humanConfig= new HumanConfig();
    private final TrainedHumanConfig trainedHumanConfig= new TrainedHumanConfig();

    private SimulationConfig(){}

    public static class Holder{
        public static final SimulationConfig instance=new SimulationConfig();
    }
    public static SimulationConfig getInstance(){
        return Holder.instance;
    }

    @Getter
    @Setter
    public static class WorldConfig{
        private volatile int width=200;
        private volatile int height=200;
        private volatile boolean paused=true;
        private volatile boolean initiated=false;
        private volatile boolean isCycling=true;
    }

    @Getter
    @Setter
    public static class VampireConfig{
        private volatile int initialNumber=20;
        private volatile int energyLoss=500;
        private volatile int energyBoost=500;
    }

    @Getter
    @Setter
    public static class HumanConfig{
        private volatile int initialNumber=20;
        private volatile int energyLoss=500;
        private volatile int energyBoost=500;
        private volatile float addProb=0f;
        private volatile float transformationProb=5f;
    }

    @Getter
    @Setter
    public static class TrainedHumanConfig{
        private volatile int initialNumber=20;
        private volatile float recruitmentProb=5f;
        private volatile float throwProb=1f;
        private volatile int garlicStockMax=3;
    }

//    public static class GarlicContainerConfig{
//        private volatile int x_min=400;
//        private volatile int x_max=599;
//        private volatile int y_min=0;
//        private volatile int y_max=200;
//    }

}

