package com.apirestjava.example.apirest.service;

import com.apirestjava.example.apirest.dto.BmiRequest;
import com.apirestjava.example.apirest.dto.BmiStatisticsResponse;
import com.apirestjava.example.apirest.entity.BMIRecord;
import com.apirestjava.example.apirest.entity.BmiCategory;
import com.apirestjava.example.apirest.exception.ForbiddenException;
import com.apirestjava.example.apirest.exception.ResourceNotFoundException;
import com.apirestjava.example.apirest.repository.BMIRecordRepository;
import com.apirestjava.example.apirest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class BmiService {

    private final BMIRecordRepository recordRepository;
    private final UserRepository userRepository;

    public BmiService(BMIRecordRepository recordRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BMIRecord calculate(Long userId, BmiRequest request) {
        ensureUserExists(userId);

        BigDecimal weight = request.getWeight().setScale(2, RoundingMode.HALF_UP);
        BigDecimal height = request.getHeight().setScale(3, RoundingMode.HALF_UP);
        BigDecimal bmi = computeBmi(weight, height);

        BMIRecord record = new BMIRecord(userId, weight, height, bmi,
                BmiCategory.fromBmi(bmi), LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<BMIRecord> getHistory(Long userId) {
        ensureUserExists(userId);
        return recordRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional
    public BMIRecord update(Long id, Long userId, BmiRequest request) {
        BMIRecord record = findOrThrow(id);
        checkOwner(record, userId);

        BigDecimal weight = request.getWeight().setScale(2, RoundingMode.HALF_UP);
        BigDecimal height = request.getHeight().setScale(3, RoundingMode.HALF_UP);
        BigDecimal bmi = computeBmi(weight, height);

        record.setWeight(weight);
        record.setHeight(height);
        record.setBmi(bmi);
        record.setCategory(BmiCategory.fromBmi(bmi));
        return recordRepository.save(record);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        BMIRecord record = findOrThrow(id);
        checkOwner(record, userId); // autorisation : seul le propriétaire peut supprimer
        recordRepository.delete(record);
    }

    @Transactional(readOnly = true)
    public BmiStatisticsResponse getStatistics() {
        long total = recordRepository.count();

        BigDecimal average = null;
        Double avg = recordRepository.averageBmi();
        if (avg != null) {
            average = BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
        }

        // Toutes les catégories apparaissent, même à 0
        Map<BmiCategory, Long> distribution = new EnumMap<>(BmiCategory.class);
        for (BmiCategory category : BmiCategory.values()) {
            distribution.put(category, 0L);
        }
        for (Object[] row : recordRepository.countByCategory()) {
            distribution.put((BmiCategory) row[0], (Long) row[1]);
        }

        return new BmiStatisticsResponse(total, average, distribution);
    }

    // --- méthodes privées ---

    private BigDecimal computeBmi(BigDecimal weight, BigDecimal height) {
        // BMI = poids (kg) / taille (m)^2
        return weight.divide(height.multiply(height), 2, RoundingMode.HALF_UP);
    }

    private BMIRecord findOrThrow(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement BMI introuvable avec l'id " + id));
    }

    private void checkOwner(BMIRecord record, Long userId) {
        if (!record.getUserId().equals(userId)) {
            throw new ForbiddenException("Vous n'êtes pas propriétaire de cet enregistrement");
        }
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur introuvable avec l'id " + userId);
        }
    }
}