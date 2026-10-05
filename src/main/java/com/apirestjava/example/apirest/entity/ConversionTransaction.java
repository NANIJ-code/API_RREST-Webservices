package com.apirestjava.example.apirest.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversion_transactions")
public class ConversionTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal amount;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal rate;

    @Column(name = "converted_amount", nullable = false, precision = 40, scale = 10)
    private BigDecimal convertedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_source", nullable = false, length = 20)
    private RateSource source;

    @Column(name = "date_transaction", nullable = false)
    private LocalDateTime date;

    public ConversionTransaction() {
    }

    public ConversionTransaction(String fromCurrency, String toCurrency, BigDecimal amount,
                                 BigDecimal rate, BigDecimal convertedAmount,
                                 RateSource source, LocalDateTime date) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
        this.source = source;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }

    public RateSource getSource() { return source; }
    public void setSource(RateSource source) { this.source = source; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}