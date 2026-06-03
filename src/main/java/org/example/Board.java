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

    public Cell getClosestCellContainingGarlicContainer(int x, int y){ //metoda, która zwraca komórkę
//        zawierającą garlicContainerCell, która znajduje się najbliżej współrzędnych x i y. Przydaje się
//        do wyznaczenia komórki celu, do której zbliża się trainedHuman, gdy chce iść do kontenera po czosnek
        Cell closestCell=null;
        Double maxDistance=Double.POSITIVE_INFINITY;
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
        }
        return closestCell;
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
