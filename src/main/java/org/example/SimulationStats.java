package org.example;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Licznik statystyk dla całego środowiska symulacji.
 * <p>
 * Klasa implementuje wzorzec projektowy Singleton, udostępniając globalny punkt
 * do instancji licznika.
 */
@Getter
public class SimulationStats {
    /**
     * Mapa przechowująca aktualną, liczbę obiektów danego typu na planszy.
     * * -- GETTER --
     * Zwraca bezpieczną wątkowo mapę ze statystykami obiektów.
     * * @return Mapa typów i odpowiadającej im liczby obiektów.
     */
    private final Map<ObjectType, Integer> objectsMap=new ConcurrentHashMap<>();
    /**
     * Mapa przechowująca sumaryczną liczbę wystąpień poszczególnych interakcji w symulacji.
     * * -- GETTER --
     * Zwraca bezpieczną wątkowo mapę z liczbami interakcji
     * * @return Mapa par typów interkacji i całkowitej liczby poszczególnych interakcji.
     */
    private final Map<InteractionType, Integer> interactionsMap=new ConcurrentHashMap<>();

    /**
     * Prywatny konstruktor blokujący możliwość tworzenia obiektu poza klasą.
     */
    private SimulationStats(){}

    /**
     klasa pomocnicza inicjalizująca instancję Singletona.
     */
    private static class Holder{
        private static final SimulationStats instance=new SimulationStats();
    }

    /**
     * Zwraca globalną instancję licznika statystyk.
     * * @return Instancja {@link SimulationStats}.
     */
    public static SimulationStats getInstance(){
        return Holder.instance;
    }

    /**
     * Inkrementuje licznik określonego typu obiektu.
     * * @param type Typ obiektu, którego licznik ma zostać zwiększony.
     */
    public void addObjectOfType(ObjectType type){
        objectsMap.merge(type, 1, Integer::sum);
    }

    /**
     * Dekrementuje licznik określonego typu obiektu, jeśli licznik istnieje i jest powyżej zera
     * * @param type Typ obiektu, którego licznik ma zostać zmniejszony.
     */
    public void removeObjectOfType(ObjectType type){
        if(objectsMap.containsKey(type) && objectsMap.get(type)>0) objectsMap.put(type, objectsMap.get(type)-1);
    }

    /**
     * Inkrementuje licznik określonego typu interkacji.
     * * @param type Typ interkacji, której licznik ma zostać zwiększony.
     */
    public void addInteractionOfType(InteractionType type){
        interactionsMap.merge(type, 1, Integer::sum);
    }
}