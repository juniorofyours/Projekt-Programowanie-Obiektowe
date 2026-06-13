package org.example;

/**
 * Interface implementowany przez startegie poruszania się agentów.
 */
public interface MovingStrategy {

    /**
     * Oblicza kolejny krok przesunięcia agenta na podstawie jego
     * aktualnego położenia i stanu symulacji.
     * * @param position   Obiekt pozycji powiązany z agentem, udostępniający metodę
     * modyfikacji współrzędnych x i y na planszy.
     * @param simulation Instancja symulacji.
     */
    void move(AgentPosition position, Simulation simulation);
}
