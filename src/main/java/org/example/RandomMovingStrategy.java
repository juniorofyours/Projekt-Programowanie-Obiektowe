package org.example;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy{ //strategia do losowego poruszania się po planszy

    @Override
    public void move(AgentPosition position, Simulation simulation){
        Random rand=new Random();
        boolean success;
        int dx;
        int dy;
        do{
            try { //tutaj występuje blok try catch łapiący błąd stworzonego w
//                pliku Cell.java typu UnusableCellException, czyli jeśli agent nie może przejść do danej komórki
//                , to losuje jeszcze raz nowe dx i dy i próbuje znowu się poruszyć
                dx=rand.nextInt(3)- 1; //losuje liczbe miedzy -1, 0 i 1
                dy =rand.nextInt(3) - 1;
                position.move(dx,dy);
                success=true;
            }catch(UnusableCellException e){
                success=false;
            }
        }while(!success);
    }
}
