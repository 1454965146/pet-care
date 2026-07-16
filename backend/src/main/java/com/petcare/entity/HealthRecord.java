package com.petcare.entity;

import com.petcare.enums.CareType;
import com.petcare.enums.MentalState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Enumerated(EnumType.STRING)
    private CareType careType;

    private Double weight;

    private Double temperature;

    @Enumerated(EnumType.STRING)
    private MentalState mentalState;

    private String note;

    private LocalDate recordDate;

}
