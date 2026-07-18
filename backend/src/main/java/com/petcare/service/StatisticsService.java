package com.petcare.service;

import com.petcare.dto.MonthlyStatisticsResponse;
import com.petcare.entity.HealthRecord;
import com.petcare.entity.Reminder;
import com.petcare.enums.CareType;
import com.petcare.enums.MentalState;
import com.petcare.repository.HealthRecordRepository;
import com.petcare.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final HealthRecordRepository healthRecordRepository;
    private final ReminderRepository reminderRepository;

    public StatisticsService(HealthRecordRepository healthRecordRepository, ReminderRepository reminderRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.reminderRepository = reminderRepository;
    }

    public MonthlyStatisticsResponse getMonthlyStatistics(Long petId) {
        LocalDate start = LocalDate.now().minusDays(29);
        LocalDate end = LocalDate.now();

        List<HealthRecord> records = healthRecordRepository.findByPetIdAndRecordDateBetweenOrderByRecordDateAsc(petId, start, end);
        List<HealthRecord> abnormalRecords = healthRecordRepository.findByPetIdAndMentalStateAndRecordDateBetweenOrderByRecordDateAsc(petId, MentalState.ILL, start, end);

        List<String> dates = records.stream()
                .map(r -> r.getRecordDate().toString())
                .collect(Collectors.toList());

        List<Double> weights = records.stream()
                .map(HealthRecord::getWeight)
                .collect(Collectors.toList());

        List<Double> temperatures = records.stream()
                .map(HealthRecord::getTemperature)
                .collect(Collectors.toList());

        List<Double> validWeights = weights.stream()
                .filter(w -> w != null)
                .collect(Collectors.toList());

        Double avgWeight = null;
        Double weightVolatility = 0.0;

        if (!validWeights.isEmpty()) {
            avgWeight = validWeights.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            if (avgWeight == 0.0) {
                weightVolatility = 0.0;
            } else {
                // ✅ 修复：用 final 变量捕获 avgWeight，供 Lambda 使用
                final double finalAvgWeight = avgWeight;
                double variance = validWeights.stream()
                        .mapToDouble(w -> Math.pow(w - finalAvgWeight, 2))
                        .average().orElse(0.0);
                weightVolatility = Math.sqrt(variance) / finalAvgWeight * 100;
            }
        }

        int abnormalCount = abnormalRecords.size();
        int totalRecords = records.size();

        // 此处的 petId 是方法参数，本身就是 effectively final，无需修改
        List<Reminder> vaccineReminders = reminderRepository.findByPetIdAndCareType(petId, CareType.VACCINE);

        long totalVaccine = vaccineReminders.size();
        long completedVaccine = vaccineReminders.stream().filter(r -> Boolean.TRUE.equals(r.getCompleted())).count();
        int vaccineCompletionRate = totalVaccine == 0 ? 100 : (int) (completedVaccine * 100 / totalVaccine);

        return MonthlyStatisticsResponse.builder()
                .dates(dates)
                .weights(weights)
                .temperatures(temperatures)
                .avgWeight(avgWeight)
                .weightVolatility(weightVolatility)
                .abnormalCount(abnormalCount)
                .totalRecords(totalRecords)
                .vaccineCompletionRate(vaccineCompletionRate)
                .build();
    }
}
