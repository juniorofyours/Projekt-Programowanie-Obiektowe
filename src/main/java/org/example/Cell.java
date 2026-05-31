package org.example;

import java.util.ArrayList;
import java.util.List;

class UnusableCellException extends RuntimeException{ //nowy typ błędu, który jest zwracany, jeśli agent
//    miał przejść na daną komórkę, ale nie mogło to się udać, przez to, że komórka jest zablokowana dla niego
//    (usable=false)
    public UnusableCellException(String message){
        super(message);
    }
}

public class Cell {
    int x;
    int y;
    List<Agent> agents;
    List<Garlic> garlics;
    Boolean usable;
    GarlicContainerCell garlicContainerCell;

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

    public List<Agent> getAgents(){
        return agents;
    }
    public List<Garlic> getGarlics(){
        return garlics;
    }

    public GarlicContainerCell getGarlicContainerCell() {
        return garlicContainerCell;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public Boolean isUsable(){
        return usable;
    }
}
