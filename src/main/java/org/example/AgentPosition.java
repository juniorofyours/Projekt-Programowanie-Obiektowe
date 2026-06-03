package org.example;

public class AgentPosition {
    int x;
    int y;
    Board board;
    Agent agent;

    public AgentPosition(Agent agent, Board board, int x, int y){
        this.agent=agent;
        this.board=board;
        this.x=x;
        this.y=y;
    }

    public void move(int dx, int dy){ //metoda zmieniająca pozycję agenta o dany dx i dy i aktualizująca
//        położenie agenta na planszy
        int newX=Math.floorMod(x+dx, board.getWidth());
        int newY=Math.floorMod(y+dy, board.getHeight());
        board.updateAgent(agent, newX, newY);
        x=newX;
        y=newY;
    }

    public int getX() {
        return x;
    }
    public int getY(){
        return y;
    }
}
