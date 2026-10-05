package com.apirestjava.example.apirest.controller;

import com.apirestjava.example.apirest.dto.ManualConversionRequest;
import com.apirestjava.example.apirest.dto.UpdateRateRequest;
import com.apirestjava.example.apirest.entity.ConversionTransaction;
import com.apirestjava.example.apirest.entity.ExchangeRate;
import com.apirestjava.example.apirest.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
public class CurrencyConverterController {

    private final CurrencyService service;

    public CurrencyConverterController(CurrencyService service) {
        this.service = service;
    }

    // Conversion avec taux externe
    @GetMapping("/convert")
    public ConversionTransaction convert(@RequestParam("amount") BigDecimal amount,
                                         @RequestParam("from") String from,
                                         @RequestParam("to") String to) {
        return service.convert(amount, from, to);
    }

    // Conversion avec taux manuel
    @PostMapping("/convert")
    public ResponseEntity<ConversionTransaction> convertManual(
            @Valid @RequestBody ManualConversionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.convertManual(request));
    }

    // Mise à jour d'un taux stocké localement
    @PutMapping("/update-rate")
    public ExchangeRate updateRate(@Valid @RequestBody UpdateRateRequest request) {
        return service.updateRate(request);
    }

    @DeleteMapping("/delete-transaction/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // Pratique pour vérifier l'état de la base pendant les tests (non demandé par le TP)
    @GetMapping("/transactions")
    public Page<ConversionTransaction> transactions(
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getTransactions(pageable);
    }
}