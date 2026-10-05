package com.apirestjava.example.apirest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BmiRequest {

    @NotNull(message = "Le champ 'weight' (kg) est obligatoire")
    @DecimalMin(value = "1", message = "'weight' doit être supérieur ou égal à 1 kg")
    @DecimalMax(value = "500", message = "'weight' doit être inférieur ou égal à 500 kg")
    private BigDecimal weight;

    @NotNull(message = "Le champ 'height' (mètres) est obligatoire")
    @DecimalMin(value = "0.3", message = "'height' doit être supérieur ou égal à 0,3 m")
    @DecimalMax(value = "2.8", message = "'height' doit être inférieur ou égal à 2,8 m")
    private BigDecimal height;

    public BmiRequest() {
    }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }
}