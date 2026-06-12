package org.example;

import lombok.Getter;

import java.util.Random;

public class AgentPosition {
    @Getter
    private int x;
    @Getter
    private int y;
    private final Board board;
    private final Agent agent;
    private Random rand;

    public AgentPosition(Agent agent, Board board, int x, int y){
        this.agent=agent;
        this.board=board;
        this.x=x;
        this.y=y;
        this.rand=new Random();
    }
    public boolean randomize(){
        Cell randomCell;
        int i=0;
        do{
            randomCell=board.getCell(rand.nextInt(board.getWidth()), rand.nextInt(board.getHeight()));
            i++;
            if(i==10) return false;
        }while(!randomCell.isUsable());
        this.x=randomCell.getX();
        this.y=randomCell.getY();
        return true;
    }

    public void move(int dx, int dy){ //metoda zmieniająca pozycję agenta o dany dx i dy i aktualizująca
//        położenie agenta na planszy
        int newX=Math.floorMod(x+dx, board.getWidth());
        int newY=Math.floorMod(y+dy, board.getHeight());
        board.updateAgent(agent, newX, newY);
        x=newX;
        y=newY;
    }

}
