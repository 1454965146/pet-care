package com.petcare.controller;

import com.petcare.dto.ApiResult;
import com.petcare.dto.HealthRecordRequest;
import com.petcare.dto.HealthRecordResponse;
import com.petcare.dto.MonthlyStatisticsResponse;
import com.petcare.dto.ReminderResponse;
import com.petcare.enums.CareType;
import com.petcare.service.HealthRecordService;
import com.petcare.service.ReminderService;
import com.petcare.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final HealthRecordService healthRecordService;
    private final StatisticsService statisticsService;
    private final ReminderService reminderService;

    @PostMapping("/health-record")
    public ApiResult<HealthRecordResponse> addHealthRecord(@RequestBody HealthRecordRequest request) {
        HealthRecordResponse response = healthRecordService.addHealthRecord(request);
        return ApiResult.success(response);
    }

    @GetMapping("/statistics/monthly")
    public ApiResult<MonthlyStatisticsResponse> getMonthlyStatistics(@RequestParam Long petId) {
        MonthlyStatisticsResponse response = statisticsService.getMonthlyStatistics(petId);
        return ApiResult.success(response);
    }

    @GetMapping("/reminders/today")
    public ApiResult<List<ReminderResponse>> getTodayReminders(@RequestParam Long petId) {
        List<ReminderResponse> responses = reminderService.getTodayReminders(petId);
        return ApiResult.success(responses);
    }

    @GetMapping("/health-records")
    public ApiResult<List<HealthRecordResponse>> getHealthRecords(
            @RequestParam Long petId,
            @RequestParam(required = false) CareType careType) {
        List<HealthRecordResponse> responses = healthRecordService.getHealthRecords(petId, careType);
        return ApiResult.success(responses);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException e) {
        return ApiResult.error(e.getMessage());
    }

}
