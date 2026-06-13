package org.example;

import lombok.Getter;

import java.util.Random;

/**
 * Abstrakcyjna klasa bazowa reprezentująca agenta w symulacji.
 * <p>
 * Każdy agent posiada unikalny identyfikator, określone położenie na planszy oraz poziom energii.
 * Poruszanie się agenta jest oddelegowane do danych klas strategii implementujących interface {@link MovingStrategy}.
 * </p>
 * <p>
 * Klasy potomne (np. {@code Human}, {@code Vampire}) muszą zaimplementować metody dotyczące
 * aktualizacji stanu wewnętrznego oraz interakcji z innymi obiektami na planszy.
 * </p>
 */
public abstract class Agent {
    protected Simulation simulation;
    protected Board board;
    protected final AgentPosition position;
    protected MovingStrategy movement;
    protected final SimulationClock clock;
    protected final SimulationStats stats;
    protected int energyLevel;
    protected int energyMax;
    @Getter
    protected int energyBoost;
    @Getter
    protected int energyLoss;
    protected final int id;
    protected Random rand;
    protected static int nextID=0;


    /**
     * Konstruuje nowego agenta i inicjalizuje jego podstawowe parametry, pozycję oraz strategię ruchu.
     * * @param simulation  Instancja symulacji.
     * @param board       Plansza, na której agent zostanie umieszczony.
     * @param x           Początkowa współrzędna x na siatce planszy.
     * @param y           Początkowa współrzędna y na siatce planszy.
     * @param energyBoost Wartość boostu energii.
     * @param energyLoss  Wartość utraty energii.
     */
    public Agent(Simulation simulation, Board board, int x, int y, int energyBoost, int energyLoss){
        this.simulation=simulation;
        this.board=board;
        this.position=new AgentPosition(this, board, x, y);
        this.energyMax=1000;
        this.energyLevel=1000;
        this.energyBoost=energyBoost;
        this.energyLoss=energyLoss;
        this.movement=new RandomMovingStrategy();
        this.id=nextID++;
        clock=SimulationClock.getInstance();
        stats=SimulationStats.getInstance();
        rand=new Random();
    }

    /**
     * Pobiera aktualną współrzędną x agenta.
     * * @return Współrzędna pozioma.
     */
    public int getX(){
        return position.getX();
    }

    /**
     * Pobiera aktualną współrzędną y agenta.
     * * @return Współrzędna pionowa.
     */
    public int getY(){
        return position.getY();
    }

    /**
     * Losuje nową, wolną pozycję dla agenta na planszy.
     */
    public void randomizePosition(){
        position.randomize();
    }

    /**
     * Metoda abstrakcyjna odpowiedzialna za aktualizację stanu wewnętrznego agenta.
     * Powinna być wywoływana w każdym takcie zegara przed wykonaniem ruchu.
     */
    public abstract void updateCurrentState();

    /**
     * Metoda abstrakcyjna odpowiedzialna za interakcję agenta z otoczeniem.
     */
    public abstract void interact();

    /**
     * Wykonuje ruch agenta na planszy zgodnie z aktualnie przypisaną strategią {@link MovingStrategy}.
     */
    public void move(){
        movement.move(position, simulation);
        System.out.println("-Krok agenta nr."+id+" ("+this.getClass().getSimpleName()+") "+"[ "+position.getX()+" "+position.getY()+" ]");
    }

    /**
     * Weryfikuje, czy agent zostanie usunięty z symulacji z powodu braku energii.
     * <p>
     * Jeśli poziom energii spadnie do zera, agent zostaje usunięty z całej symulacji.
     * Metoda zostaje nadpisana w klasie reprezentującej ludzi, aby umożliwić
     * scenariusz transformacji człowieka w wampira zamiast standardowej śmierci.
     * </p>
     * * @return {@code true} jeśli agent umarł i został usunięty; {@code false} jeśli nie został usunięty.
     */
    public boolean tryRemove(){
        if(energyLevel==0) {
            simulation.removeAgent(this);
            ConsoleColors.printlnRed("<<Smierc agenta>>");
            return true;
        }
        return false;
    }

    /**
     * Zwiększa aktualny poziom energii agenta o zadaną wartość, pilnując by nie została przekroczona
     * maksymalna dopuszczalna ilość energii ({@code energyMax}).
     * * @param energyBoost Ilość punktów energii, o którą ma zostać wzmocniony agent.
     */
    protected void boostEnergy(int energyBoost){
        energyLevel=Math.min(energyLevel+energyBoost, energyMax);
    }

    /**
     * Zmniejsza aktualny poziom energii agenta o zadaną wartość, pilnując by poaiom energii nie spadł poniżej 0
     * * @param energyLoss Ilość punktów energii, którą agent traci.
     */
    protected void loseEnergy(int energyLoss){
        energyLevel=Math.max(energyLevel-energyLoss, 0);
    }
}
