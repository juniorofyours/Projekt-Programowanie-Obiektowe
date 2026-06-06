package org.example;

public class CollectMovingStrategy implements MovingStrategy{ //strategia poruszania się dla wytrenowanych
//    ludzi, gdy muszą dobrać czosnek z kontenera
    private Board board;
    public CollectMovingStrategy(Board board){
        this.board=board;
    }

    //wektor torusowy dla danej osi:
    public static int vectorTorus (int coord, int goal, int sizeOfBoard) {
        //delta - odpowiednik standardowej drogi
        int delta = goal - coord;

        //sprawdzamy, czy zwykla droga jest wieksza niz polowa rozmiaru planszy
        if(Math.abs(delta) > sizeOfBoard /2.0) { //jezeli tak, to trzeba zawinąć
            if(delta > 0) { // skrót przez lewa / gorna (w zaleznosci od osi) krawedz
                return delta - sizeOfBoard;
            }
            else { // skrót przez prawa / dolna (w zaleznosci od osi) krawedz
                return delta + sizeOfBoard;
            }
        }
        return delta; //droga standardowa
    }

    @Override
    public void move(AgentPosition position, Simulation simulation){
        int[] targetCoords = simulation.getCoordinatesOfContainer(); //0 - minX, 1 - maxX, 2 - minY, 3 - maxY
        Cell targetCell=board.getClosestCellContainingGarlicContainer(position.getX(), position.getY(), targetCoords[0],  targetCoords[1],  targetCoords[2],  targetCoords[3]);

        int x = position.getX(), y = position.getY();

        int width = board.getWidth() + 1;
        int height = board.getHeight() + 1;
        int dx = vectorTorus(x, targetCell.getX(), width);
        int dy = vectorTorus(y, targetCell.getY(), height);

        if(dx <= 1 && dx >= -1) {
            if(dy <= 1 && dy >= -1) {
                return;
            }
            else {
                int nextY = y + Integer.signum(dy);
                nextY = (nextY + height) % height;
                position.move(x, nextY);
                return;
            }
        }
        else {
            if (dy <= 1 && dy >= -1) {
                int nextX = x + Integer.signum(dx);
                nextX = (nextX + width) % width;
                position.move(nextX, y);
                return;
            }
            else {
                int nextX = x + Integer.signum(dx);
                nextX = (nextX + width) % width;
                int nextY = y + Integer.signum(dy);
                nextY = (nextY + height) % height;
                position.move(nextX, nextY);
            }
        }
        /*
        int x_diff=targetCell.getX()-position.getX();
        int y_diff=targetCell.getY()-position.getY();
        if(x_diff<=1&&y_diff<=1){ //jeśli agent znajduje się centralnie przy najbliższym garlicContainerCell,
//            to nic się nie dzieje, bo agent już nie musi się poruszać p oplanszy
            return;
        }

//        agent jest przesuwany w kierunku kontenra (najbliższego garlicContainerCell)
        int dx=(int)Math.signum(x_diff);
        int dy=(int)Math.signum(y_diff);
        position.move(dx,dy);*/

    }
}
