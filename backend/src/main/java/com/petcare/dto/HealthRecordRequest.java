package com.petcare.dto;

import com.petcare.enums.CareType;
import com.petcare.enums.MentalState;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthRecordRequest {

    private Long petId;

    private CareType careType;

    private Double weight;

    private Double temperature;

    private MentalState mentalState;

    private String note;

    private LocalDate recordDate;

}
