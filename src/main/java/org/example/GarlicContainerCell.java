package org.example;

import lombok.Getter;

/**
 * Reprezentuje pojedynczą komórkę składową kontenera na czosnek.
 * <p>
 * Obiekty tej klasy są dodawane do planszy ({@link Board}) wewnątrz
 * odpowiednich obiektów {@link Cell}.
 * Ich obecność powoduje zablokowanie ruchu agentów na danym polu planszy.
 * </p>
 */
@Getter
public class GarlicContainerCell {
    private final int x;
    private final int y;

    /**
     * Konstruuje komórkę kontenera
     * @param x współrzędna x komórki
     * @param y współrzędna y komórki
     */
    public GarlicContainerCell(int x, int y){
        this.x=x;
        this.y=y;
    }
}
