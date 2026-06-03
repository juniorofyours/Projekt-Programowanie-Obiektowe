package org.example;

import java.util.Random;

public abstract class Agent {
    protected Simulation simulation;
    protected Board board;
    protected AgentPosition position;
    protected MovingStrategy movement;
    protected int energyLevel;
    protected int energyMax;
    protected int energyBoost;
    protected int energyLoss;
    protected static int nextID=0;
    protected int id;
    protected Random rand=new Random();

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
    }

    public int getX(){
        return position.getX();
    }
    public int getY(){
        return position.getY();
    }

    public abstract void updateCurrentState(); //metoda, która zostanie zaimplementowana w klasach
//    poszczególnych agentów aktualizująca ich stan
    public abstract void interact(); //metoda, która zostanie zaimplementowana w klasach
//    poszczególnych agentów przeprowadzająca ich interkacje z otoczeniem

    public void move(){ //metoda wywołująca metodę move dla konkretnej strategii poruszania się
        movement.move(position);
        System.out.println("-Krok agenta nr."+id+" ("+this.getClass().getSimpleName()+") "+"[ "+position.getX()+" "+position.getY()+" ]");
    }
    public boolean tryRemove(){ //metoda suuswająca agenta, jesli ma energię=0, powinna być nadpisana dla
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
    public int getEnergyBoost() {
        return energyBoost;
    }

    public int getEnergyLoss() {
        return energyLoss;
    }
}
