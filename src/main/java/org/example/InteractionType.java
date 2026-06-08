package org.example;

import lombok.Getter;

@Getter
public enum InteractionType {
    ATTACK("Ataki wampirów na ludzi"),
    GARLIC_THROW("Rozrzucenia czosnku"),
    RECRUITMENT("Rekrutacje"),
    NEW_HUMAN_BIRTH("Urodzenia nowych ludzi"),
    GARLIC_EAT("Pobranie czosnku przez ludzi"),
    GARLIC_ATTACK("Ataki czosnku na wampiry"),
    TRANSFORMATION("Transformacje ludzi w wampiry");

    private final String description;
    InteractionType(String description){
        this.description=description;
    }
}
