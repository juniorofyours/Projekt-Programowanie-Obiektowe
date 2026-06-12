package org.example;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SimulationStats {
    private final Map<ObjectType, Integer> objectsMap=new ConcurrentHashMap<>();
    private final Map<InteractionType, Integer> interactionsMap=new ConcurrentHashMap<>();

    private SimulationStats(){}

    private static class Holder{
        private static final SimulationStats instance=new SimulationStats();
    }
    public static SimulationStats getInstance(){
        return Holder.instance;
    }

    public void addObjectOfType(ObjectType type){
        objectsMap.merge(type, 1, Integer::sum);
    }

    public void removeObjectOfType(ObjectType type){
        if(objectsMap.containsKey(type) && objectsMap.get(type)>0) objectsMap.put(type, objectsMap.get(type)-1);
    }

    public void addInteractionOfType(InteractionType type){
        interactionsMap.merge(type, 1, Integer::sum);
    }
}