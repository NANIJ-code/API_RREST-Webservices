package com.apirestjava.example.apirest.entity;

import java.math.BigDecimal;

public enum BmiCategory {
    INSUFFISANCE_PONDERALE,   // BMI < 18,5
    NORMAL,                   // 18,5 <= BMI < 25
    SURPOIDS,                 // 25 <= BMI < 30
    OBESITE;                  // BMI >= 30

    private static final BigDecimal LIMIT_UNDER = new BigDecimal("18.5");
    private static final BigDecimal LIMIT_NORMAL = new BigDecimal("25");
    private static final BigDecimal LIMIT_OVER = new BigDecimal("30");

    public static BmiCategory fromBmi(BigDecimal bmi) {
        if (bmi.compareTo(LIMIT_UNDER) < 0) {
            return INSUFFISANCE_PONDERALE;
        }
        if (bmi.compareTo(LIMIT_NORMAL) < 0) {
            return NORMAL;
        }
        if (bmi.compareTo(LIMIT_OVER) < 0) {
            return SURPOIDS;
        }
        return OBESITE;
    }
}