package org.example;

import lombok.Getter;

/**
 * Typ Enum reprezentujący typ interakcji między agentami i czosnkiem.
 * <p>
 * Służy głównie do zbierania danych statystycznych dotyczących liczby poszczególnych interakcji.
 * </p>
 */
@Getter
public enum InteractionType {
    ATTACK("Ataki wampirów na ludzi"),
    GARLIC_THROW("Rozrzucenia czosnku"),
    RECRUITMENT("Rekrutacje"),
    NEW_HUMAN_BIRTH("Urodzenia nowych ludzi"),
    GARLIC_EAT("Pobranie czosnku przez ludzi"),
    GARLIC_ATTACK("Ataki czosnku na wampiry"),
    TRANSFORMATION("Transformacje ludzi w wampiry");

    /**
     * Tekstowy opis danego typu interakcji.
     * * -- GETTER --
     * Pobiera pełny opis tekstowy interakcji.
     * @return Tekstowy opis interakcji.
     */
    private final String description;

    /**
     * Konstruktor wewnętrzny powiązujący element enuma z jego opisem.
     * * @param description Opis tekstowy
     */
    InteractionType(String description){
        this.description=description;
    }
}
