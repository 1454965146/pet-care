package com.petcare.controller;

import com.petcare.dto.ApiResult;
import com.petcare.entity.Pet;
import com.petcare.entity.Reminder;
import com.petcare.repository.HealthRecordRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetInfoController {

    private final PetRepository petRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final ReminderRepository reminderRepository;

    @GetMapping("/list")
    public ApiResult<List<Pet>> listPets() {
        List<Pet> pets = petRepository.findAll();
        return ApiResult.success(pets);
    }

    @PostMapping("/add")
    public ApiResult<Pet> addPet(@RequestBody Pet pet) {
        Pet saved = petRepository.save(pet);
        return ApiResult.success(saved);
    }

    
    @PostMapping("/reminder/add")
    public ApiResult<Reminder> addReminder(@RequestBody Map<String, Object> body) {
        Long petId = Long.valueOf(body.get("petId").toString());
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        Reminder reminder = new Reminder();
        reminder.setPet(pet);
        reminder.setTitle((String) body.get("title"));
        reminder.setReminderDate(java.time.LocalDate.parse(body.get("reminderDate").toString()));
        reminder.setCompleted(false);
        reminder.setExpired(false);
        if (body.containsKey("careType") && body.get("careType") != null) {
            reminder.setCareType(com.petcare.enums.CareType.valueOf(body.get("careType").toString()));
        }
        Reminder saved = reminderRepository.save(reminder);
        return ApiResult.success(saved);
    }

    @PutMapping("/reminder/complete/{id}")
    public ApiResult<Reminder> completeReminder(@PathVariable Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("待办不存在"));
        reminder.setCompleted(true);
        Reminder updated = reminderRepository.save(reminder);
        return ApiResult.success(updated);
    }

    @PutMapping("/reminder/update/{id}")
    public ApiResult<Reminder> updateReminder(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Reminder existing = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("待办不存在"));
        if (body.containsKey("title") && body.get("title") != null) {
            existing.setTitle((String) body.get("title"));
        }
        if (body.containsKey("reminderDate") && body.get("reminderDate") != null) {
            existing.setReminderDate(java.time.LocalDate.parse(body.get("reminderDate").toString()));
        }
        if (body.containsKey("careType") && body.get("careType") != null) {
            existing.setCareType(com.petcare.enums.CareType.valueOf(body.get("careType").toString()));
        }
        if (body.containsKey("completed") && body.get("completed") != null) {
            existing.setCompleted(Boolean.valueOf(body.get("completed").toString()));
        }
        Reminder updated = reminderRepository.save(existing);
        return ApiResult.success(updated);
    }

    @DeleteMapping("/reminder/{id}")
    public ApiResult<Void> deleteReminder(@PathVariable Long id) {
        reminderRepository.deleteById(id);
        return ApiResult.success(null);
    }

}
