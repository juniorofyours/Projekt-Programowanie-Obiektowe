package org.example;

public class Garlic { //typ Garlic nawet nie przechowuje metody tryRemove i pola eaten, bo agenci
//    jedzący lub atakowani przez czosnek, mogą od razu usunąć ten czosnek z listy garlics w symulacji i z planszy
    //przez wywołanie metody simulation.removeGarlic()
    private Simulation simulation;
    private Board board;
    private final int x;
    private final int y;


    public Garlic(Simulation simulation, Board board, int x, int y){
        this.simulation=simulation;
        this.board=board;
        this.x=x;
        this.y=y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
