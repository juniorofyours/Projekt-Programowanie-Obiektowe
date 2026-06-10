package org.example;

import lombok.Getter;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SimulationStats {
//    volatile private int interactionsNumber=0;
private ConcurrentHashMap<ObjectType, Integer> objectsMap=new ConcurrentHashMap<>();
    private ConcurrentHashMap<InteractionType, Integer> interactionsMap=new ConcurrentHashMap<>();

    private SimulationStats(){}

    public static class Holder{
        public static final SimulationStats instance=new SimulationStats();
    }
    public static SimulationStats getInstance(){
        return Holder.instance;
    }

//    public void addInteraction(){
//        interactionsNumber++;
//    }
    public void addObjectOfType(ObjectType type){
        objectsMap.merge(type, 1, Integer::sum);
    }
    public void removeObjectOfType(ObjectType type){
        if(objectsMap.containsKey(type) && objectsMap.get(type)>0) objectsMap.put(type, objectsMap.get(type)-1);
    }

    public void addInteractionOfType(InteractionType type){
        interactionsMap.merge(type, 1, Integer::sum);
    }
//    public void removeInteractionOfType(InteractionType type){
//        if(objectsNumber.containsKey(type) && objectsNumber.get(type)>0) interactionsNumber.put(type, objectsNumber.get(type)-1);
//    }

}