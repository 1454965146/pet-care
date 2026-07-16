package com.petcare.repository;

import com.petcare.entity.HealthRecord;
import com.petcare.enums.CareType;
import com.petcare.enums.MentalState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByPetIdOrderByRecordDateDesc(Long petId);

    List<HealthRecord> findByPetIdAndCareTypeOrderByRecordDateDesc(Long petId, CareType careType);

    List<HealthRecord> findByPetIdAndRecordDateBetweenOrderByRecordDateAsc(Long petId, LocalDate start, LocalDate end);

    List<HealthRecord> findByPetIdAndMentalStateAndRecordDateBetweenOrderByRecordDateAsc(Long petId, MentalState mentalState, LocalDate start, LocalDate end);

}
