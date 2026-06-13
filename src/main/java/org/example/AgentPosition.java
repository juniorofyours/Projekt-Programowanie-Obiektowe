package org.example;

import lombok.Getter;

import java.util.Random;


/**
 * Reprezentuje i zarządza pozycją pojedynczego agenta.
 */
public class AgentPosition {
    @Getter
    private int x;
    @Getter
    private int y;
    private final Board board;
    private final Agent agent;
    private Random rand;

    /**
     * Konstruuje nowy obiekt pozycji dla konkretnego agenta na wskazanej planszy.
     * * @param agent Instancja agenta, do którego przypisana jest ta pozycja.
     * @param board Plansza symulacji, na której znajduje się agent.
     * @param x     Początkowa współrzędna x.
     * @param y     Początkowa współrzędna y.
     */
    public AgentPosition(Agent agent, Board board, int x, int y){
        this.agent=agent;
        this.board=board;
        this.x=x;
        this.y=y;
        this.rand=new Random();
    }

    /**
     * Losuje nową, poprawną pozycję dla agenta na planszy.
     * <p>
     * Metoda wykonuje maksymalnie 10 prób wylosowania komórki siatki. Sprawdza, czy
     * dana komórka nadaje się do użytku ({@code isUsable()}). Jeśli w ciągu 10 prób
     * nie uda się znaleźć wolnego miejsca, aktualne współrzędne agenta nie są zmieniane
     * </p>
     * * @return {@code true} jeśli pomyślnie znaleziono i ustawiono nową pozycję;
     * {@code false} jeśli po 10 próbach nie udało się znaleźć zdatnej komórki.
     */
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

    /**
     * Zmienia aktualne położenie agenta na planszy o zadany wektor przesunięcia (delta x i delta y).
     * <p>
     * Metoda wykorzystuje operację {@link Math#floorMod(int, int)},
     * co gwarantuje prawidłowe zawijanie pozycji na krawędziach
     * Po wyliczeniu nowych współrzędnych aktualizuje stan komórek w obiekcie {@link Board}.
     * </p>
     * * @param dx Przesunięcie wzdłuż osi x (liczba komórek w lewo/prawo).
     * @param dy Przesunięcie wzdłuż osi y (liczba komórek w górę/dół).
     */
    public void move(int dx, int dy){
        int newX=Math.floorMod(x+dx, board.getWidth());
        int newY=Math.floorMod(y+dy, board.getHeight());
        board.updateAgent(agent, newX, newY);
        x=newX;
        y=newY;
    }

}
