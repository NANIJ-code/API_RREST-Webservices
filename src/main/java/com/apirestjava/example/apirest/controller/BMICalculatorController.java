package com.apirestjava.example.apirest.controller;

import com.apirestjava.example.apirest.dto.BmiRequest;
import com.apirestjava.example.apirest.dto.BmiStatisticsResponse;
import com.apirestjava.example.apirest.entity.BMIRecord;
import com.apirestjava.example.apirest.security.AuthenticatedUser;
import com.apirestjava.example.apirest.service.BmiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bmi")
public class BMICalculatorController {

    private final BmiService service;

    public BMICalculatorController(BmiService service) {
        this.service = service;
    }

    // L'utilisateur authentifié est extrait du JWT par JwtAuthenticationFilter
    @PostMapping("/calculate")
    public ResponseEntity<BMIRecord> calculate(@AuthenticationPrincipal AuthenticatedUser user,
                                               @Valid @RequestBody BmiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calculate(user.id(), request));
    }

    @GetMapping("/history")
    public List<BMIRecord> history(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getHistory(user.id());
    }

    @PutMapping("/update/{id}")
    public BMIRecord update(@PathVariable Long id,
                            @AuthenticationPrincipal AuthenticatedUser user,
                            @Valid @RequestBody BmiRequest request) {
        return service.update(id, user.id(), request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal AuthenticatedUser user) {
        service.delete(id, user.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public BmiStatisticsResponse statistics() {
        return service.getStatistics();
    }
}