package org.example;

import lombok.Getter;

/**
 * Zegar zarządzający upływem czasu oraz cyklem dobowym (dniem i nocą) w symulacji.
 * <p>
 * Klasa implementuje wzorzec projektowy Singleton, zapewniając globalny
 * punkt dostępu do zasobów czasu dla wszystkich agentów i silnika renderującego.
 * Pola czasu są modyfikowane przez główny wątek symulacji i oznaczone jako {@code volatile},
 * co gwarantuje natychmiastową widoczność zmian dla wątku GUI.
 * </p>
 */
@Getter
public class SimulationClock {
    private final SimulationConfig config=SimulationConfig.getInstance();
    private volatile int step=0;
    private volatile float hour=0;
    private final int stepsPerHour=50;
    private final float sunsetHour=18;
    private final float sunriseHour=6;
    private volatile boolean night=true;

    /**
     * Prywatny konstruktor blokujący możliwość tworzenia obiektu poza klasą.
     */
    private SimulationClock(){}

    /**
    klasa pomocnicza inicjalizująca instancję Singletona.
     */
    private static class Holder{
        private static final SimulationClock instance=new SimulationClock();
    }

    /**
     * Zwraca globalną instancję zegara symulacji.
     * * @return Instancja {@link SimulationClock}.
     */
    public static SimulationClock getInstance(){
        return Holder.instance;
    }

    /**
     * Inkrementuje licznik kroków symulacji oraz aktualizuje godzinę.
     * <p>
     * Jeśli w konfiguracji włączone jest cykliczne zmienianie czasu, metoda oblicza nową
     * godzinę na podstawie parametru {@code stepsPerHour} i automatycznie przełącza pole
     * {@code night} po przekroczeniu godzin granicznych wschodu lub zachodu słońca.
     * </p>
     */
    public void updateClock(){
        step++;
        if(!config.getWorldConfig().isCycling())return;

        hour=(hour+ 1.0f/stepsPerHour)%24;
        if(hour>=sunriseHour&&hour<sunsetHour) night=false;
        else night=true;
    }

    /**
     * Wymusza ustawienie nocy w symulacji.
     * Przestawia zegar dokładnie na godzinę zachodu słońca i ustawia pole {@code night} na {@code true}.
     */
    public void setNight(){
        night=true;
        hour=sunsetHour;
    }

    /**
     * Wymusza ustawienie dnia w symulacji.
     * Przestawia zegar dokładnie na godzinę wschodu słońca i ustawia pole {@code night} na {@code false}.
     */
    public void setDay(){
        night=false;
        hour=sunriseHour;
    }


}
