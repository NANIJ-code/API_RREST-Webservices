package com.apirestjava.example.apirest.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OperationType {
    ADDITION, SOUSTRACTION, MULTIPLICATION, DIVISION;

    // Accepte "division", "Division", "DIVISION"...
    @JsonCreator
    public static OperationType from(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Opération inconnue : " + value));
    }

    // Renvoie "division" (minuscules) dans le JSON
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}