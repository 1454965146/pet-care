package com.petcare.service;

import com.petcare.dto.HealthRecordRequest;
import com.petcare.dto.HealthRecordResponse;
import com.petcare.entity.HealthRecord;
import com.petcare.entity.Pet;
import com.petcare.enums.CareType;
import com.petcare.repository.HealthRecordRepository;
import com.petcare.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final PetRepository petRepository;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, PetRepository petRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.petRepository = petRepository;
    }


    public HealthRecordResponse addHealthRecord(HealthRecordRequest request) {
        if (request.getWeight() != null && (request.getWeight() < 0 || request.getWeight() > 100)) {
            throw new RuntimeException("体重数据异常");
        }
        if (request.getTemperature() != null && (request.getTemperature() < 30 || request.getTemperature() > 45)) {
            throw new RuntimeException("体温数据异常");
        }
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        HealthRecord record = new HealthRecord();
        record.setPet(pet);
        record.setCareType(request.getCareType());
        record.setWeight(request.getWeight());
        record.setTemperature(request.getTemperature());
        record.setMentalState(request.getMentalState());
        record.setNote(request.getNote());
        record.setRecordDate(request.getRecordDate());

        HealthRecord saved = healthRecordRepository.save(record);
        return toResponse(saved);
    }

    public List<HealthRecordResponse> getHealthRecords(Long petId, CareType careType) {
        List<HealthRecord> records;
        if (careType != null) {
            records = healthRecordRepository.findByPetIdAndCareTypeOrderByRecordDateDesc(petId, careType);
        } else {
            records = healthRecordRepository.findByPetIdOrderByRecordDateDesc(petId);
        }
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private HealthRecordResponse toResponse(HealthRecord record) {
        HealthRecordResponse response = new HealthRecordResponse();
        response.setId(record.getId());
        response.setPetId(record.getPet().getId());
        response.setPetName(record.getPet().getName());
        response.setCareTypeName(record.getCareType().name());
        response.setWeight(record.getWeight());
        response.setTemperature(record.getTemperature());
        response.setMentalStateName(record.getMentalState().name());
        response.setNote(record.getNote());
        response.setRecordDate(record.getRecordDate());
        return response;
    }
}
