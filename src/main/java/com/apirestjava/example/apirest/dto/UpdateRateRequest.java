package com.apirestjava.example.apirest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class UpdateRateRequest {

    @NotNull(message = "Le champ 'from' est obligatoire")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "'from' doit être un code devise de 3 lettres (ex. EUR)")
    private String from;

    @NotNull(message = "Le champ 'to' est obligatoire")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "'to' doit être un code devise de 3 lettres (ex. USD)")
    private String to;

    @NotNull(message = "Le champ 'rate' est obligatoire")
    @DecimalMin(value = "0", inclusive = false, message = "'rate' doit être strictement positif")
    @DecimalMax(value = "1000000", message = "'rate' est trop grand")
    private BigDecimal rate;

    public UpdateRateRequest() {
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
}