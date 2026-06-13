package org.example;

import lombok.Getter;


/**
 * Reprezentuje kontener na czosnek w przestrzeni symulacji.
 * <p>
 * Kontener definiuje prostokątny obszar na planszy wyznaczony przez współrzędne graniczne
 * (od {@code x_min} do {@code x_max} oraz od {@code y_min} do {@code y_max}). Składa się z dwuwymiarowej
 * siatki komórek {@link GarlicContainerCell}, które po dodanie do komórek planszy
 * ({@link Cell}) blokują ruch agentów.
 * </p>
 */
@Getter
public class GarlicContainer {
    private GarlicContainerCell[][] containerCells;
    private int width;
    private int height;
//    współrzędne graniczne kontenera:
    private int x_min;
    private int x_max;
    private int y_min;
    private int y_max;

    /**
     * Konstruuje nowy kontener na czosnek o określonych współrzędnych granicznych na planszy.
     * <p>
     * Automatycznie oblicza szerokość i wysokość obszaru, inicjalizuje wewnętrzną tablicę
     * komórek i wywołuje metodę {@link #fillContainer()} w celu jej zapełnienia.
     * </p>
     * * @param x_min Lewa krawędź obszaru (indeks kolumny).
     * @param x_max Prawa krawędź obszaru (indeks kolumny).
     * @param y_min Górna krawędź obszaru (indeks wiersza).
     * @param y_max Dolna krawędź obszaru (indeks wiersza).
     */
    public GarlicContainer(int x_min, int x_max, int y_min, int y_max){
        this.width=x_max-x_min+1;
        this.height=y_max-y_min+1;
        this.x_min=x_min;
        this.x_max=x_max;
        this.y_min=y_min;
        this.y_max=y_max;
        containerCells=new GarlicContainerCell[width][height];
        fillContainer();
    }

    /**
     * Wypełnia tablicę wewnętrzną kontenera nowo utworzonymi komórkami kontenera.
     * <p>
     * Każda komórka {@link GarlicContainerCell} otrzymuje współrzędne
     * odpowiadające jej rzeczywistej pozycji na planszy.
     * </p>
     */
    private void fillContainer(){
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                containerCells[i][j]=new GarlicContainerCell(i+x_min, j+y_min);
            }
        }
    }

    /**
     * Zwraca komórkę kontenera znajdującą się w podanym miejscu na planszy.
     * * @param x Globalna współrzędna x na planszy.
     * @param y Globalna współrzędna y na planszy.
     * @return Obiekt {@link GarlicContainerCell} przypisany do podanych współrzędnych.
     * @throws IndexOutOfBoundsException Jeśli podane współrzędne wykraczają poza kontener.
     */
    public GarlicContainerCell getContainerCell(int x, int y){
        return containerCells[x-x_min][y-y_min];
    }

}