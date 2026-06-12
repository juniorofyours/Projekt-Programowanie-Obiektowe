package org.example;

import lombok.Getter;

@Getter
public class GarlicContainerCell {
    private final int x;
    private final int y;
    public GarlicContainerCell(int x, int y){
        this.x=x;
        this.y=y;
    }
}
