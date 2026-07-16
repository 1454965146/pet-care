package com.petcare.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReminderResponse {

    private Long id;

    private String title;

    private LocalDate reminderDate;

    private Boolean expired;

    private String careTypeName;

    private Boolean completed;

}
