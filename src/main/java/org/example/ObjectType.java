package org.example;

import lombok.Getter;

@Getter
public enum ObjectType {
    VAMPIRE("Wampiry"),
    HUMAN("Zwykli ludzie"),
    TRAINED_HUMAN("Wytrenowani ludzie"),
    GARLIC("Czosnek"),
    GARLIC_CONTAINER_CELL("Komórki kontenera czosnku");

    private final String description;
    ObjectType(String description){
        this.description=description;
    }
}
