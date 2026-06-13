package org.example;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Wyjątek rzucany w sytuacji, gdy agent próbuje wejść na komórkę planszy,
 * która jest niedostępna (np. z powodu zablokowania przez kontener na czosnek).
 */
class UnusableCellException extends RuntimeException{
    /**
     * Konstruuje nowy wyjątek z określonym komunikatem.
     * * @param message Komunikat błędu.
     */
    public UnusableCellException(String message){
        super(message);
    }
}

/**
 * Reprezentuje pojedynczą komórkę na dwuwymiarowej planszy symulacji.
 * <p>
 * Komórka może przechowywać listy agentów
 * ({@link Agent}) i czosnku ({@link Garlic}) jednocześnie znajdujących się
 * na tej samej pozycji. Zarządza dostępnością {@code usable}, która blokuje
 * możliwość wchodzenia obiektów na teren zajęty przez({@link GarlicContainerCell}).
 * </p>
 */
@Getter
public class Cell {
    private final int x;
    private final int y;
    private final List<Agent> agents;
    private final List<Garlic> garlics;
    private boolean usable;
    private GarlicContainerCell garlicContainerCell;

    /**
     * Konstruuje nową komórkę o określonych współrzędnych.
     * * @param x Współrzędna x na siatce planszy.
     * @param y Współrzędna y na siatce planszy.
     */
    public Cell(int x, int y){
        this.x=x;
        this.y=y;
        agents=new ArrayList<Agent>();
        garlics=new ArrayList<Garlic>();
        this.usable=true;
        this.garlicContainerCell=null;
    }

    /**
     * usuwa agenta z lokalnej listy danej komórki.
     * * @param agent Obiekt agenta do usnięcia.
     */
    public void removeAgent(Agent agent){ //usuwa agenta ze swojej listy agents
        agents.remove(agent);
    }

    /**
     * Dodaje nowego agenta do listy obecności w danej komórce.
     * * @param agent Obiekt agenta wchodzącego na komórkę.
     * @throws UnusableCellException Jeśli komórka ma ustawione {@code usable = false}
     * (jest zajęta przez komórkę kontenera).
     */
    public void addAgent(Agent agent){
        if(!usable) throw new UnusableCellException("Cell ["+x+", "+y+"] does not accept agents");
        agents.add(agent);
    }

    /**
     * Usuwa obiekt czosnku z komórki.
     * * @param garlic Obiekt czosnku do usunięcia.
     */
    public void removeGarlic(Garlic garlic){
        garlics.remove(garlic);
    }

    /**
     * Umieszcza nowy obiekt czosnku na komórce.
     * * @param garlic Obiekt czosnku rozrzucany w tym miejscu.
     * @throws UnusableCellException Jeśli komórka jest niedostępna
     */
    public void addGarlic(Garlic garlic){
        if(!usable) throw new UnusableCellException("Cell ["+x+", "+y+"] does not accept garlics");
       garlics.add(garlic);
    }

    /**
     * Dodaje komórkę kontenera na czosnek i automatycznie blokuje komórkę.
     * <p>
     * Po wywołaniu tej metody, pole {@code usable} przyjmuje wartość {@code false}, co uniemożliwia
     * wchodzenie agentom oraz umieszczanie czosnku w tej komórce planszy.
     * </p>
     * * @param cell Obiekt reprezentujący komórkę kontenera.
     */
    public void addGarlicContainerCell(GarlicContainerCell cell){
        this.garlicContainerCell=cell;
        this.usable=false;
    }

    /**
     * Usuwa komórkę kontenera z tej komórki i przywraca dostępność dla agentów i czosnku.
     */
    public void removeGarlicContainerCell(){
        this.garlicContainerCell=null;
        this.usable=true;
    }

    /**
     * Pobiera pierwszy priorytetowy obiekt znajdujący się w komórce, służący później do renderowania graficznego.
     * <p>
     * Wybór obiektu odbywa się według hierarchii ważności:
     * <ol>
     * <li>Pierwszy agent z listy</li>
     * <li>Pierwszy czosnek z listy (jeśli nie ma agentów)</li>
     * <li>Komórka kontenera (jeśli nie ma agentów i czosnku)</li>
     * </ol>
     * </p>
     * * @return Obiekt typu {@link Agent}, {@link Garlic} lub {@link GarlicContainerCell} albo {@code null}, jeśli komórka jest pusta.
     */
    public Object getFirstObject(){
        if(agents.size()!=0) return agents.get(0);
        if(garlics.size()!=0) return garlics.get(0);
        if(garlicContainerCell!=null) {
            return garlicContainerCell;
        }
        return null;
    }
}
