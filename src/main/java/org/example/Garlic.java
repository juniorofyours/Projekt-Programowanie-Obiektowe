package org.example;

import lombok.Getter;


/**
 * Klasa reprezentująca obiekt czosnku rozrzuconego na planszy symulacji.
 * <p>
 * Zjedzenie czosnku przez człowieka zapewnia zwiększenie energii oraz czasową ochronę
 * przed wampirami, a zjedzenie przez wampira zmniejsza jego energię.
 * Obiekt ma niezmienne położenie x i y.
 * </p>
 */
public class Garlic {
    @Getter
    private final int x;
    @Getter
    private final int y;

    /**
     * Konstruuje nowy obiekt czosnku i przypisuje do określonych współrzędnych na planszy.
     * @param x          Współrzędna X (kolumna) dedykowana dla tego obiektu.
     * @param y          Współrzędna Y (wiersz) dedykowana dla tego obiektu.
     */
    public Garlic(int x, int y){
        this.x=x;
        this.y=y;
    }
}
