package org.example;

import lombok.Getter;

@Getter
public class SimulationClock {
    private final SimulationConfig config=SimulationConfig.getInstance();
    private volatile int step=0;
    private volatile float hour=0;
    private final int stepsPerHour=50;
    private final float sunsetHour=18;
    private final float sunriseHour=6;
    private volatile boolean night=true;

    private SimulationClock(){}

    private static class Holder{
        private static final SimulationClock instance=new SimulationClock();
    }
    public static SimulationClock getInstance(){
        return Holder.instance;
    }

    public void updateClock(){
        step++;
        if(!config.getWorldConfig().isCycling())return;

        hour=(hour+ 1.0f/stepsPerHour)%24;
        if(hour>=sunriseHour&&hour<sunsetHour) night=false;
        else night=true;
    }
    public void setNight(){
        night=true;
        hour=sunsetHour;
    }
    public void setDay(){
        night=false;
        hour=sunriseHour;
    }


}
