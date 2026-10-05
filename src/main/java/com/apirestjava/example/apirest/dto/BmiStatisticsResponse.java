package com.apirestjava.example.apirest.dto;

import com.apirestjava.example.apirest.entity.BmiCategory;

import java.math.BigDecimal;
import java.util.Map;

public record BmiStatisticsResponse(
        long totalRecords,
        BigDecimal averageBmi,
        Map<BmiCategory, Long> distribution
) {
}