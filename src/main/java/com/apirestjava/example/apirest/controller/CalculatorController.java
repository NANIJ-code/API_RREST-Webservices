package com.apirestjava.example.apirest.controller;

import com.apirestjava.example.apirest.dto.OperationRequest;
import com.apirestjava.example.apirest.entity.Operation;
import com.apirestjava.example.apirest.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Operation> calculate(@Valid @RequestBody OperationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calculate(request));
    }

    @GetMapping("/history")
    public Page<Operation> history(
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getHistory(pageable);
    }

    @PutMapping("/{id}")
    public Operation update(@PathVariable Long id, @Valid @RequestBody OperationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}