package org.example;

import java.util.List;

public class Board {
    private Cell[][] grid;
    private int width;
    private int height;

    public Board(int width, int height){
        grid=new Cell[width][height];
        this.width=width;
        this.height=height;
        fillGrid();
    }

    private void fillGrid(){ //metoda która tworzy nowe komórki w tablicy grid
        for(int i=0; i<width;i++){
            for(int j=0;j<height;j++){
                grid[i][j]=new Cell(i, j);
            }
        }
    }
    public void updateAgent(Agent agent, int newX, int newY){ //metoda, która dodaje agenta do nowej komórki
//        o współrzędnych newX i newY i usuwa agenta ze starej komórki
        int x=agent.getX();
        int y=agent.getY();
        grid[newX][newY].addAgent(agent);
        grid[x][y].removeAgent(agent);
    }
    public void removeFromBoard(Agent agent){ //usuwa agenta z komórki w której się znajduje
        int x=agent.getX();
        int y=agent.getY();

        grid[x][y].removeAgent(agent);
    }
    public void addToBoard(Agent agent){ //dodaje agenta do komórki o danych współrzędnych
        int x=agent.getX();
        int y=agent.getY();

        grid[x][y].addAgent(agent);
    }

    public void replaceAgentInCell(Agent agentToBeReplaced, Agent newAgent){
        int x=agentToBeReplaced.getX();
        int y=agentToBeReplaced.getY();

        List<Agent> agents=grid[x][y].getAgents();
        int index=agents.indexOf(agentToBeReplaced);
        agents.set(index, newAgent);
    }

    public void removeGarlicFromBoard(Garlic garlic){ //usuwa czosnek z komórki
        int x=garlic.getX();
        int y=garlic.getY();

        grid[x][y].removeGarlic(garlic);
    }
    public void addGarlicToBoard(Garlic garlic){ //dodaje czosnek do komórki
        int x=garlic.getX();
        int y=garlic.getY();

        grid[x][y].addGarlic(garlic);
    }

    public void addGarlicContainer(GarlicContainer container){ //dodaje kontener do planszy,
//        czyli wypełnia komórki planszy komórkami kontenera
        for(int i=container.getX_min(); i<=container.getX_max();i++){
            for(int j=container.getY_min(); j<=container.getY_max();j++){
                grid[i][j].addGarlicContainerCell(container.getContainerCell(i, j));
            }
        }
    }
    public void removeGarlicContainer(GarlicContainer container){ //usuwa cały kontener z planszy,
//        czyli wszystkie komórki kontenera z komórek planszy
        for(int i=container.getX_min(); i<=container.getX_max();i++){
            for(int j=container.getY_min(); j<=container.getY_max();j++){
                grid[i][j].removeGarlicContainerCell();
            }
        }
    }

    //dla przesztreni torusowej:
    public static int getClosestCoordinate(int coord, int min, int max, int sizeOfBoard) {
        //sprawdzanie dystansu bez zawijania w przestrzeni torusowej
        int basic = Math.max(min, Math.min(coord, max)); //najblizszy punkt w standardowym ruchu
        int basicDistance = distanceTorus(coord, basic, sizeOfBoard);

        //sprawdzenie odleglosci przez lewa / dolna krawedz planszy (min):
        int distanceThroughMin = distanceTorus(coord, min, sizeOfBoard);

        //sprawdzenie odleglosci przez prawa / gorna krawedz planszy (max):
        int distanceThroughMax = distanceTorus(coord, max, sizeOfBoard);

        int resultCoord=basic;
        int resultDistance = basicDistance;

        if(distanceThroughMin < resultDistance) {
            resultCoord = min;
            resultDistance = distanceThroughMin;
        }
        if(distanceThroughMax < resultDistance) {
            resultCoord = max;
        }

        return resultCoord;
    }

    //zwraca liczbe komorek, ktorą należy pokonać, aby droga była jak najmniejsza (uwzględniając charakter torusowy planszy)
    public static int distanceTorus(int coord1, int coord2, int sizeOfBoard) { //dziala dla jednej osi
        int distance = Math.abs(coord1-coord2);
        return Math.min(distance, sizeOfBoard + 1 - distance); //zwraca dystans bliższy (czy droga standardowa, czy zawijajaca jest lepsza)
    }

    //minX, maxX itd - sa to wspolrzedne rogów kontenera
    public Cell getClosestCellContainingGarlicContainer(int x, int y, int minX, int maxX, int minY, int maxY){ //metoda, która zwraca komórkę
//        zawierającą garlicContainerCell, która znajduje się najbliżej współrzędnych x i y. Przydaje się
//        do wyznaczenia komórki celu, do której zbliża się trainedHuman, gdy chce iść do kontenera po czosnek
        //Cell closestCell=null;

        int nearestX = getClosestCoordinate(x, minX, maxX, this.getWidth());
        int nearestY = getClosestCoordinate(y, minY, maxY, this.getHeight());


        /*Double maxDistance=Double.POSITIVE_INFINITY;
        Double distance;
        for(int i=0; i<width;i++){
            for(int j=0; j<height;j++){
                if(grid[i][j].getGarlicContainerCell()!=null){
                    distance=Math.pow(Math.pow(i-x, 2)+Math.pow(j-y, 2), 2);
                    if(distance<maxDistance) {
                        closestCell = grid[i][j];
                        maxDistance=distance;
                    }
                }
            }
        }*/
        return grid[nearestX][nearestY];
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public Cell getCell(int x, int y){
        return grid[x][y];
    } //zwraca komórkę o danych współrzędnych
}
