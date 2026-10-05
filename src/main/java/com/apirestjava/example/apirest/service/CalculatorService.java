package com.apirestjava.example.apirest.service;

import com.apirestjava.example.apirest.dto.OperationRequest;
import com.apirestjava.example.apirest.entity.Operation;
import com.apirestjava.example.apirest.entity.OperationType;
import com.apirestjava.example.apirest.exception.InvalidOperationException;
import com.apirestjava.example.apirest.exception.ResourceNotFoundException;
import com.apirestjava.example.apirest.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CalculatorService {

    private final OperationRepository repository;

    public CalculatorService(OperationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Operation calculate(OperationRequest request) {
        BigDecimal resultat = compute(request.getOperation(), request.getA(), request.getB());

        Operation operation = new Operation(
                request.getOperation(),
                request.getA(),
                request.getB(),
                resultat,
                LocalDateTime.now()
        );

        return repository.save(operation);
    }

    @Transactional(readOnly = true)
    public Page<Operation> getHistory(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Operation update(Long id, OperationRequest request) {
        Operation operation = findOrThrow(id);

        operation.setOperateur(request.getOperation());
        operation.setA(request.getA());
        operation.setB(request.getB());
        operation.setResultat(compute(request.getOperation(), request.getA(), request.getB()));

        return repository.save(operation);
    }

    @Transactional
    public void delete(Long id) {
        Operation operation = findOrThrow(id);
        repository.delete(operation);
    }

    private Operation findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opération introuvable avec l'id " + id));
    }

    private BigDecimal compute(OperationType type, BigDecimal a, BigDecimal b) {
        return switch (type) {
            case ADDITION -> a.add(b);
            case SOUSTRACTION -> a.subtract(b);
            case MULTIPLICATION -> a.multiply(b);
            case DIVISION -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new InvalidOperationException("La division par zéro n'est pas autorisée");
                }
                yield a.divide(b, 10, RoundingMode.HALF_UP);
            }
        };
    }
}