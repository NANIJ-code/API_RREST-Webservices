package com.apirestjava.example.apirest.repository;

import com.apirestjava.example.apirest.entity.ConversionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionTransactionRepository extends JpaRepository<ConversionTransaction, Long> {
}