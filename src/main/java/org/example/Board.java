package org.example;

import lombok.Getter;

import java.util.List;

/**
 * Reprezentuje dwuwymiarową planszę (siatkę) symulacji składającą się z komórek {@link Cell}.
 * <p>
 * Klasa odpowiada za zarządzanie rozmieszczeniem agentów (ludzi, wampirów)
 * oraz innych obiektów (czosnku i kontenera) na siatce.
 * </p>
 */
public class Board {
    private Cell[][] grid;
    @Getter
    private int width;
    @Getter
    private int height;

    /**
     * Konstruuje nową planszę o zadanych wymiarach i automatycznie wypełnia ją komórkami.
     * * @param width  Szerokość planszy.
     * @param height Wysokość planszy.
     */
    public Board(int width, int height){
        grid=new Cell[width][height];
        this.width=width;
        this.height=height;
        fillGrid();
    }

    /**
     * Inicjalizuje tablicę siatki, tworząc nową instancję obiektu {@link Cell}
     * dla każdej pary współrzędnych x i y.
     */
    private void fillGrid(){
        for(int i=0; i<width;i++){
            for(int j=0;j<height;j++){
                grid[i][j]=new Cell(i, j);
            }
        }
    }

    /**
     * Przemieszcza agenta na planszy do nowej komórki.
     * Dodaje obiekt agenta do nowej komórki oraz usuwa go z dotychczasowej lokalizacji.
     * * @param agent  Obiekt agenta, który zmienia pozycję.
     * @param newX   Nowa współrzędna x docelowej komórki.
     * @param newY   Nowa współrzędna y docelowej komórki.
     */
    public void updateAgent(Agent agent, int newX, int newY){
        int x=agent.getX();
        int y=agent.getY();
        grid[newX][newY].addAgent(agent);
        grid[x][y].removeAgent(agent);
    }

    /**
     * Usuwa agenta z komórki planszy, w której się aktualnie znajduje.
     * * @param agent Agent przeznaczony do usunięcia z komórki.
     */
    public void removeFromBoard(Agent agent){
        int x=agent.getX();
        int y=agent.getY();

        grid[x][y].removeAgent(agent);
    }

    /**
     * Umieszcza agenta w komórce odpowiadającej jego współrzędnym x i y;
     * * @param agent Agent do dodania do komórki
     */
    public void addToBoard(Agent agent){
        int x=agent.getX();
        int y=agent.getY();

        grid[x][y].addAgent(agent);
    }

    /**
     * Dokonuje podmiany agentów wewnątrz listy agentów konkretnej komórki.
     * <p>
     * Metoda wyszukuje indeks starego agenta w liście komórki oraz wstawia nowego agenta
     * na ten sam indeks. Wykorzystywana przy transformacjach agentów (np. zamiana człowieka w wampira)
     * </p>
     * * @param agentToBeReplaced Dotychczasowy agent, który ma zostać podmieniony.
     * @param newAgent          Nowy obiekt agenta do podstawienia pod starego agenta
     */
    public void replaceAgentInCell(Agent agentToBeReplaced, Agent newAgent){
        int x=agentToBeReplaced.getX();
        int y=agentToBeReplaced.getY();

        List<Agent> agents=grid[x][y].getAgents();
        int index=agents.indexOf(agentToBeReplaced);
        agents.set(index, newAgent);
    }

    /**
     * Usuwa obiekt czosnku z komórki planszy, na której się znajdował.
     * * @param garlic Obiekt czosnku przeznaczony do usunięcia.
     */
    public void removeGarlicFromBoard(Garlic garlic){
        int x=garlic.getX();
        int y=garlic.getY();

        grid[x][y].removeGarlic(garlic);
    }

    /**
     * Umieszcza obiekt czosnku w komórce siatki na podstawie jego współrzędnych.
     * * @param garlic Obiekt czosnku, który ma zostać dodany do planszy.
     */
    public void addGarlicToBoard(Garlic garlic){
        int x=garlic.getX();
        int y=garlic.getY();

        grid[x][y].addGarlic(garlic);
    }

    /**
     * Nanosi kontener na czosnek na siatkę planszy.
     * Iteruje po komórkach kontenera i dodaje te komórki kontenera
     * do komórek planszy.
     * * @param container Obiekt kontenera przechowujący granice i swoje komórki.
     */
    public void addGarlicContainer(GarlicContainer container){
        for(int i=container.getX_min(); i<=container.getX_max();i++){
            for(int j=container.getY_min(); j<=container.getY_max();j++){
                grid[i][j].addGarlicContainerCell(container.getContainerCell(i, j));
            }
        }
    }

    /**
     * Usuwa strukturę kontenera na czosnek z planszy.
     * * @param container Obiekt kontenera, który ma być usunięty z planszy.
     */
    public void removeGarlicContainer(GarlicContainer container){
        for(int i=container.getX_min(); i<=container.getX_max();i++){
            for(int j=container.getY_min(); j<=container.getY_max();j++){
                grid[i][j].removeGarlicContainerCell();
            }
        }
    }

    /**
     * Wyznacza najbliższą komórkę dla zadanego przedziału [{@param min}, {@param max}] dla agenta znajdującego się
     * na komórce ze współrzędną {@param coord}. Działa tylko dla jednej zadanej osi (OX lub OY). Uwzględnia charakter
     * torusowy planszy.
     * @param coord                     Współrzędna, na której znajduje się agent.
     * @param min                       Współrzędna, która ma najmniejszą wartość z zadanego przedziału.
     * @param max                       Współrzędna, która ma najmniejszą wartość z zadanego przedziału.
     * @param sizeOfBoard               Rozmiar planszy w danej osi (OX lub OY).
     * @return najbliższa komórka       Komórka z zadanego przedziału planszy, która jest
     * najbliżej agenta wg zadanej osi.
     */
    public static int getClosestCoordinate(int coord, int min, int max, int sizeOfBoard) {
        //sprawdzanie dystansu bez zawijania w przestrzeni torusowej
        int basic = Math.max(min, Math.min(coord, max)); //najbliższy punkt w standardowym ruchu
        int basicDistance = distanceTorus(coord, basic, sizeOfBoard);

        //sprawdzenie odległości przez lewą / dolna krawędź planszy (min):
        int distanceThroughMin = distanceTorus(coord, min, sizeOfBoard);
        //sprawdzenie odległości przez prawą / górna krawędź planszy (max):
        int distanceThroughMax = distanceTorus(coord, max, sizeOfBoard);
        //zmienna, która zostanie zwrócona:
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

    /**
     * Zwraca liczbę komórek, którą należy pokonać, aby droga była jak najmniejsza,
     * uwzględniając charakter torusowy planszy. Działa na jednej wybranej osi (OX lub OY)
     * @param coord1                    Współrzędna jednej komórki.
     * @param coord2                    Współrzędna drugiej komórki.
     * @param sizeOfBoard               Rozmiar planszy w danej osi (OX lub OY).
     * @return dystans bliższy          Dystans, który jest mniejszy do pokonania (czyli droga standardowa lub
     * zawijająca i przechodząca przez krawędź planszy).
     */
    public static int distanceTorus(int coord1, int coord2, int sizeOfBoard) {
        int distance = Math.abs(coord1-coord2);
        return Math.min(distance, sizeOfBoard - distance);
    }

    /**
     * Zwraca najbliższą do agenta komórkę kontenera, korzystając z metody {@code getClosestCoordinate()}.
     * <p>
     *     Dana metoda jest używana dla wyznaczenia drogi do kontenera przez osobę wyszkoloną.
     * </p>
     * @param x                         Współrzędna komórki, na której znajduje się agent (oś OX).
     * @param y                         Współrzędna komórki, na której znajduje się agent (oś OY).
     * @param minX                      Współrzędna komórki, w której zaczyna się kontener (oś OX).
     * @param maxX                      Współrzędna komórki, w której kończy się kontener (oś OX).
     * @param minY                      Współrzędna komórki, w której zaczyna się kontener (oś OY).
     * @param maxY                      Współrzędna komórki, w której kończy się kontener (oś OY).
     * @return najbliższa do agenta komórka kontenera
     */
    public Cell getClosestCellContainingGarlicContainer(int x, int y, int minX, int maxX, int minY, int maxY){

        int nearestX = getClosestCoordinate(x, minX, maxX, this.getWidth());
        int nearestY = getClosestCoordinate(y, minY, maxY, this.getHeight());

        return grid[nearestX][nearestY];
    }

    /**
     * Pobiera z siatki obiekt komórki o wskazanych współrzędnych.
     * @param x Współrzędna pozioma planszy
     * @param y Współrzędna pionowa planszy
     * @return Obiekt {@link Cell} o podanych współrzędnych.
     */
    public Cell getCell(int x, int y){
        return grid[x][y];
    }
}
