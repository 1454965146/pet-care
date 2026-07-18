package com.petcare.service;

import com.petcare.dto.ReminderResponse;
import com.petcare.entity.Reminder;
import com.petcare.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public List<ReminderResponse> getTodayReminders(Long petId) {
        List<Reminder> unexpired = reminderRepository.findByPetIdAndCompletedFalseAndExpiredFalseOrderByReminderDateAsc(petId);
        List<Reminder> todayReminders = reminderRepository.findByReminderDateAndCompletedFalse(LocalDate.now());

        Set<Long> seen = new LinkedHashSet<>();
        List<ReminderResponse> result = new ArrayList<>();

        for (Reminder r : unexpired) {
            if (r.getPet() == null) continue;
            if (seen.add(r.getId())) {
                result.add(toResponse(r));
            }
        }
        for (Reminder r : todayReminders) {
            if (r.getPet() == null) continue;
            if (seen.add(r.getId())) {
                result.add(toResponse(r));
            }
        }

        return result;
    }


    private ReminderResponse toResponse(Reminder reminder) {
        ReminderResponse response = new ReminderResponse();
        response.setId(reminder.getId());
        response.setTitle(reminder.getTitle());
        response.setReminderDate(reminder.getReminderDate());
        response.setExpired(reminder.getExpired());
        response.setCareTypeName(reminder.getCareType() != null ? reminder.getCareType().name() : "");
        response.setCompleted(reminder.getCompleted());
        return response;
    }
}
