package org.example;

import lombok.Getter;


/**
 * Typ Enum reprezentujący typ obiektu znajdującego się na planszy symulacji.
 * <p>
 * Jest wykorzystywany głównie przez Record {@link ObjectToRender} przechowujący informację o typie obiektu do wyrenderowania
 * na planszy oraz przez klasę {@link SimulationStats} zliczającą aktualną liczbę poszczególnych agentów i czosnku w symulacji.
 * </p>
 */
@Getter
public enum ObjectType {
    VAMPIRE("Wampiry"),
    HUMAN("Zwykli ludzie"),
    TRAINED_HUMAN("Wytrenowani ludzie"),
    GARLIC("Czosnek"),
    GARLIC_CONTAINER_CELL("Komórki kontenera czosnku");

    /**
     * Tekstowy opis danego typu obiektu.
     * * -- GETTER --
     * Pobiera pełny opis typu.
     * @return Tekstowy opis typu.
     */
    private final String description;

    /**
     * Konstruktor wewnętrzny powiązujący element enuma z jego opisem.
     * * @param description Opis tekstowy
     */
    ObjectType(String description){
        this.description=description;
    }
}
