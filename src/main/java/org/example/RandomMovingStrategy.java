package org.example;

import java.util.Random;

/**
 * Strategia poruszania się realizująca ruch losowy agenta na planszy.
 * <p>
 * Klasa implementuje interfejs {@link MovingStrategy}. W każdym kroku symulacji
 * losuje wektor przesunięcia w pionie i poziomie w zakresie od -1 do 1. Strategia
 * bezpiecznie ponawia próby poruszania się w przypadku natrafienia na komórkę zablokowaną (wyłączoną z ruchu).
 * (np. obszar zajęty przez kontener na czosnek).
 * </p>
 */
public class RandomMovingStrategy implements MovingStrategy{

    /**
     * Losuje wektor poruszania się agenta po planszy.
     * <p>
     * Metoda losuje wartości {@code dx} i {@code dy} z przedziału [-1, 0, 1] i próbuje zaktualizować
     * pozycję. Jeśli komórka jest zablokowana (rzuca {@link UnusableCellException}),
     * algorytm wykonuje do 10 prób wylosowania alternatywnego, wolnego kierunku. W przypadku
     * przekroczenia limitu prób (np. gdy agent jest osaczony przez zablokowane komórki), ruch zostaje pominięty.
     * </p>
     * * @param position   Obiekt pozycji agenta, na którym wywoływana jest próba przemieszczenia.
     * @param simulation Instancja symulacji.
     */
    @Override
    public void move(AgentPosition position, Simulation simulation){
        Random rand=new Random();
        boolean success;
        int dx;
        int dy;
        int i=0;
        do{
            try {
                dx=rand.nextInt(3)- 1;
                dy =rand.nextInt(3) - 1;
                position.move(dx,dy);
                success=true;
            }catch(UnusableCellException e){
                i++;
                if(i==10) break;
                success=false;
            }
        }while(!success);
    }
}
