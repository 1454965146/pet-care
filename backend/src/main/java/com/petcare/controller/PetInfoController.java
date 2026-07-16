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
    public ApiResult<Reminder> addReminder(@RequestBody Reminder reminder) {
        Reminder saved = reminderRepository.save(reminder);
        return ApiResult.success(saved);
    }

}
