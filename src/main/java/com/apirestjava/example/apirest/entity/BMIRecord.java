package com.apirestjava.example.apirest.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bmi_records")
public class BMIRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // en kg
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    // en mètres
    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal height;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal bmi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BmiCategory category;

    @Column(name = "date_record", nullable = false)
    private LocalDateTime date;

    public BMIRecord() {
    }

    public BMIRecord(Long userId, BigDecimal weight, BigDecimal height,
                     BigDecimal bmi, BmiCategory category, LocalDateTime date) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.category = category;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public BigDecimal getBmi() { return bmi; }
    public void setBmi(BigDecimal bmi) { this.bmi = bmi; }

    public BmiCategory getCategory() { return category; }
    public void setCategory(BmiCategory category) { this.category = category; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}