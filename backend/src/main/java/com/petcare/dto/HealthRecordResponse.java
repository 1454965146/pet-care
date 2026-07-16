package com.petcare.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthRecordResponse {

    private Long id;

    private Long petId;

    private String petName;

    private String careTypeName;

    private Double weight;

    private Double temperature;

    private String mentalStateName;

    private String note;

    private LocalDate recordDate;

}
