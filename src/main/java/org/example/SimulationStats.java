package org.example;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class SimulationStats {
    volatile private int interactionsNumber=0;
    private HashMap<ObjectType, Integer> objectsNumber=new HashMap<>();

    private SimulationStats(){}

    public static class Holder{
        public static final SimulationStats instance=new SimulationStats();
    }
    public static SimulationStats getInstance(){
        return Holder.instance;
    }

    public void addInteraction(){
        interactionsNumber++;
    }
    public void addObjectOfType(ObjectType type){
        objectsNumber.merge(type, 1, Integer::sum);
    }
    public void removeObjectOfType(ObjectType type){
        if(objectsNumber.containsKey(type) && objectsNumber.get(type)>0) objectsNumber.put(type, objectsNumber.get(type)-1);
    }

}