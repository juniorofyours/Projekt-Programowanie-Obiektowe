package org.example;

import lombok.Getter;
import lombok.Setter;


/**
 * Klasa konfiguracji odpowiedzialna za przechowywanie parametrów symulacji.
 * <p>
 * Klasa implementuje wzorzec projektowy <b>Singleton</b>, zapewniając globalny punkt dostępu
 * do konfiguracji świata, wampirów, ludzi oraz wyszkolonych ludzi. Wszystkie parametry w klasach
 * wewnętrznych są oznaczone słowem kluczowym {@code volatile}, co umożliwia bezpieczną
 * modyfikację ustawień w wątku GUI podczas trwania symulacji.
 * </p>
 */
@Getter
public class SimulationConfig {
    private final WorldConfig worldConfig =new WorldConfig();
    private final VampireConfig vampireConfig=new VampireConfig();
    private final HumanConfig humanConfig= new HumanConfig();
    private final TrainedHumanConfig trainedHumanConfig= new TrainedHumanConfig();

    /**
     * Prywatny konstruktor blokujący możliwość tworzenia obiektu poza klasą.
     */
    private SimulationConfig(){}

    /**
     klasa pomocnicza inicjalizująca instancję Singletona.
     */
    private static class Holder{
        private static final SimulationConfig instance=new SimulationConfig();
    }

    /**
     * Zwraca globalną instancję klasy konfiguracji.
     * * @return Instancja {@link SimulationConfig}.
     */
    public static SimulationConfig getInstance(){
        return Holder.instance;
    }

    /**
     * Klasa konfiguracyjna przechowująca parametry świata symulacji.
     */
    @Getter
    @Setter
    public static class WorldConfig{
        private volatile int width=300;
        private volatile int height=200;
        private volatile boolean paused=true;
        private volatile boolean initiated=false;
        private volatile boolean isCycling=true;
    }

    /**
     * Klasa konfiguracyjna przechowująca parametry wampirów.
     */
    @Getter
    @Setter
    public static class VampireConfig{
        private volatile int initialNumber=20;
        private volatile int energyLoss=500;
        private volatile int energyBoost=500;
    }

    /**
     * Klasa konfiguracyjna przechowująca parametry ludzi.
     */
    @Getter
    @Setter
    public static class HumanConfig{
        private volatile int initialNumber=20;
        private volatile int energyLoss=500;
        private volatile int energyBoost=500;
        private volatile float addProb=0.01f;
        private volatile float transformationProb=10f;
    }

    /**
     * Klasa konfiguracyjna przechowująca parametry wytrenowanych ludzi.
     */
    @Getter
    @Setter
    public static class TrainedHumanConfig{
        private volatile int initialNumber=20;
        private volatile float recruitmentProb=5f;
        private volatile float throwProb=0.1f;
        private volatile int garlicStockMax=3;
    }

}

