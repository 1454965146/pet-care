package com.petcare.entity;

import com.petcare.enums.CareType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private String title;

    private LocalDate reminderDate;

    private Boolean completed;

    private Boolean expired;

    @Enumerated(EnumType.STRING)
    private CareType careType;

}
