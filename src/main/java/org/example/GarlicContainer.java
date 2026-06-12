package org.example;

import lombok.Getter;

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
    private void fillContainer(){ //wypełnia kontener komórkami kontenera
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                containerCells[i][j]=new GarlicContainerCell(i+x_min, j+y_min);
            }
        }
    }

    public GarlicContainerCell getContainerCell(int x, int y){
        return containerCells[x-x_min][y-y_min];
    }

}