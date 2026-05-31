package org.example;

public class CollectMovingStrategy implements MovingStrategy{ //strategia poruszania się dla wytrenowanych
//    ludzi, gdy muszą dobrać czosnek z kontenera
    private Board board;
    public CollectMovingStrategy(Board board){
        this.board=board;
    }
    @Override
    public void move(AgentPosition position){
        Cell targetCell=board.getClosestCellContainingGarlicContainer(position.getX(), position.getY());

        int x_diff=targetCell.getX()-position.getX();
        int y_diff=targetCell.getY()-position.getY();
        if(x_diff<=1&&y_diff<=1){ //jeśli agent znajduje się centralnie przy najbliższym garlicContainerCell,
//            to nic się nie dzieje, bo agent już nie musi się poruszać p oplanszy
            return;
        }

//        agent jest przesuwany w kierunku kontenra (najbliższego garlicContainerCell)
        int dx=(int)Math.signum(x_diff);
        int dy=(int)Math.signum(y_diff);
        position.move(dx,dy);

    }
}
