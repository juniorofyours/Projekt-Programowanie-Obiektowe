package org.example;

import lombok.Getter;

import java.util.Random;

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

    public int getX(){
        return position.getX();
    }
    public int getY(){
        return position.getY();
    }

    public void randomizePosition(){
        position.randomize();
    }

    public abstract void updateCurrentState(); //metoda, która zostanie zaimplementowana w klasach
//    poszczególnych agentów aktualizująca ich stan
    public abstract void interact(); //metoda, która zostanie zaimplementowana w klasach
//    poszczególnych agentów przeprowadzająca ich interkacje z otoczeniem

    public void move(){ //metoda wywołująca metodę move dla konkretnej strategii poruszania się
        movement.move(position, simulation);
        System.out.println("-Krok agenta nr."+id+" ("+this.getClass().getSimpleName()+") "+"[ "+position.getX()+" "+position.getY()+" ]");
    }
    public boolean tryRemove(){ //metoda ususwająca agenta, jesli ma energię=0, powinna być nadpisana dla
//        ludzi by z danym prawdopodobienstwem zamienic ich w wampiry
        if(energyLevel==0) {
            simulation.removeAgent(this);
            ConsoleColors.printlnRed("<<Smierc agenta>>");
            return true;
        }
        return false;
    }
    protected void boostEnergy(int energyBoost){ //metoda zwiększająca energię agenta o daną ilość
//        określoną w arguemncie
        energyLevel+=energyBoost;
        if(energyLevel>energyMax) energyLevel=energyMax;
    }
    protected void loseEnergy(int energyLoss){ //metoda zmniejszająca energię agenta o daną ilość
//        określoną w arguemncie
        energyLevel-=energyLoss;
        if(energyLevel<0) energyLevel=0;
    }
}
