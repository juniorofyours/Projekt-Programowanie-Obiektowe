package org.example;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

class UnusableCellException extends RuntimeException{ //nowy typ błędu, który jest zwracany, jeśli agent
//    miał przejść na daną komórkę, ale nie mogło to się udać, przez to, że komórka jest zablokowana dla niego
//    (usable=false)
    public UnusableCellException(String message){
        super(message);
    }
}

@Getter
public class Cell {
    private final int x;
    private final int y;
    private final List<Agent> agents;
    private final List<Garlic> garlics;
    private boolean usable;
    private GarlicContainerCell garlicContainerCell;

    public Cell(int x, int y){
        this.x=x;
        this.y=y;
        agents=new ArrayList<Agent>();
        garlics=new ArrayList<Garlic>();
        this.usable=true;
        this.garlicContainerCell=null;
    }
    public void removeAgent(Agent agent){ //usuwa agenta ze swojej listy agents
        agents.remove(agent);
    }
    public void addAgent(Agent agent){ //dodaje agenta do swojej listy agents. Jeśli komórka nie jest
//        usable, czyli nie mogą na niej przebywać agenci (czyli gdy na komórce jest kontener na czosnek), zwracany
//        jest bład mówiący o tym, że dodanie agenta do komórki nie mogło się powieść
        if(!usable) throw new UnusableCellException("Cell ["+x+", "+y+"] does not accept agents");
        agents.add(agent);
    }
    public void removeGarlic(Garlic garlic){
        garlics.remove(garlic);
    } //usuwa czosnek
    public void addGarlic(Garlic garlic){ //dodaje czosnek w bliźniaczy sposób do dodania agentów
        if(!usable) throw new UnusableCellException("Cell ["+x+", "+y+"] does not accept garlics");
       garlics.add(garlic);
    }

    public void addGarlicContainerCell(GarlicContainerCell cell){ //dodaje do komórki garlicContainerCell
//        i zmienia stan komórki, na usable=false, czyli agenci nie mogą wchodzić na tę komórkę
        this.garlicContainerCell=cell;
        this.usable=false;
    }
    public void removeGarlicContainerCell(){ //usuwa garlicContainerCell i z powrotem umożliwia agentom
//        wchodzenie na komórkę
        this.garlicContainerCell=null;
        this.usable=true;
    }
    public Object getFirstObject(){
        if(agents.size()!=0) return agents.get(0);
        if(garlics.size()!=0) return garlics.get(0);
        if(garlicContainerCell!=null) {
            return garlicContainerCell;
        }
        return null;
    }
}
