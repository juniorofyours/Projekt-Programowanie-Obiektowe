package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.jupiter.api.Assertions.*;


public class SimulationStatsTest {
    SimulationStats stats=SimulationStats.getInstance();
    Map<ObjectType, Integer> objectMap;

    @BeforeEach
    void setup(){
        objectMap=stats.getObjectsMap();
        objectMap.clear();
    }

    @Test
    void testAddObjectOfType(){
        stats.addObjectOfType(ObjectType.GARLIC);
        assertTrue(stats.getObjectsMap().containsKey(ObjectType.GARLIC), "Mapa obiektów powinna zawierać klucz dodanego obiektu");

        stats.addObjectOfType(ObjectType.GARLIC);
        assertEquals(2, stats.getObjectsMap().get(ObjectType.GARLIC), "Liczba obiektów danego typu powinna być zwiększana o 1");
    }

    @Test
    void testRemoveObjectOfType(){
        objectMap.put(ObjectType.GARLIC, 1);
        stats.removeObjectOfType(ObjectType.GARLIC);
        assertEquals(0, stats.getObjectsMap().get(ObjectType.GARLIC), "Liczba obiektó danego typu powinna być zmniejszana o 1");

        stats.removeObjectOfType(ObjectType.GARLIC);
        assertEquals(0, stats.getObjectsMap().get(ObjectType.GARLIC), "Liczba obiektów danego typu nie powinna być ujemna");
    }
}
