package org.example;

public class Main {
    static void main() {

        Simulation simulation=new Simulation(400, 400);
        simulation.init();
        for(int i=0;i<20;i++) simulation.step();
    }
}
