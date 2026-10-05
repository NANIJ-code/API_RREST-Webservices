package com.apirestjava.example.apirest.repository;

import com.apirestjava.example.apirest.entity.BMIRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BMIRecordRepository extends JpaRepository<BMIRecord, Long> {

    List<BMIRecord> findByUserIdOrderByDateDesc(Long userId);

    @Query("select avg(r.bmi) from BMIRecord r")
    Double averageBmi();

    // Chaque ligne : [BmiCategory, Long]
    @Query("select r.category, count(r) from BMIRecord r group by r.category")
    List<Object[]> countByCategory();
}