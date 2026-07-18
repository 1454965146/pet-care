package com.petcare.repository;

import com.petcare.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByPetIdAndCompletedFalseAndExpiredFalseOrderByReminderDateAsc(Long petId);

    List<Reminder> findByReminderDateAndCompletedFalse(LocalDate date);

    List<Reminder> findByCompletedFalseAndReminderDateBefore(LocalDate date);

    List<Reminder> findByPetIdAndCareType(Long petId, com.petcare.enums.CareType careType);

    void deleteByPetIsNull();

}
