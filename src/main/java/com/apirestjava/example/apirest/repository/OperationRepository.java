package com.apirestjava.example.apirest.repository;

import com.apirestjava.example.apirest.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}