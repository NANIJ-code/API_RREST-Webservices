package com.apirestjava.example.apirest.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operateur;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal a;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal b;

    @Column(nullable = false, precision = 40, scale = 10)
    private BigDecimal resultat;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime date;

    // Constructeur sans argument (obligatoire pour JPA)
    public Operation() {
    }

    public Operation(OperationType operateur, BigDecimal a, BigDecimal b,
                     BigDecimal resultat, LocalDateTime date) {
        this.operateur = operateur;
        this.a = a;
        this.b = b;
        this.resultat = resultat;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationType getOperateur() {
        return operateur;
    }

    public void setOperateur(OperationType operateur) {
        this.operateur = operateur;
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

    public BigDecimal getResultat() {
        return resultat;
    }

    public void setResultat(BigDecimal resultat) {
        this.resultat = resultat;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}