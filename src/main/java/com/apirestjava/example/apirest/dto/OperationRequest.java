package com.apirestjava.example.apirest.dto;

import com.apirestjava.example.apirest.entity.OperationType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OperationRequest {

    @NotNull(message = "Le champ 'a' est obligatoire")
    @DecimalMin(value = "-1000000", message = "'a' doit être supérieur ou égal à -1 000 000")
    @DecimalMax(value = "1000000", message = "'a' doit être inférieur ou égal à 1 000 000")
    private BigDecimal a;

    @NotNull(message = "Le champ 'b' est obligatoire")
    @DecimalMin(value = "-1000000", message = "'b' doit être supérieur ou égal à -1 000 000")
    @DecimalMax(value = "1000000", message = "'b' doit être inférieur ou égal à 1 000 000")
    private BigDecimal b;

    @NotNull(message = "Le champ 'operation' est obligatoire (addition, soustraction, multiplication, division)")
    private OperationType operation;

    public OperationRequest() {
    }

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
        this.a = a;
    }

    public BigDecimal getB() {
        return b;
    }

    public void setB(BigDecimal b) {
        this.b = b;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }
}