package com.petcare.config;

import com.petcare.entity.HealthRecord;
import com.petcare.entity.Pet;
import com.petcare.entity.Reminder;
import com.petcare.enums.CareType;
import com.petcare.enums.MentalState;
import com.petcare.repository.HealthRecordRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PetRepository petRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public void run(String... args) {
        if (petRepository.count() > 0) {
            return;
        }

        Pet pet = new Pet();
        pet.setName("小橘");
        pet.setSpecies("猫");
        pet.setBreed("橘猫");
        pet.setBirthDate(LocalDate.of(2023, 3, 15));
        pet.setGender("公");
        pet = petRepository.save(pet);

        LocalDate today = LocalDate.now();
        CareType[] careTypes = {CareType.FEEDING, CareType.BATH, CareType.VACCINE, CareType.DEWORMING};
        for (int i = 0; i < 10; i++) {
            HealthRecord record = new HealthRecord();
            record.setPet(pet);
            record.setRecordDate(today.minusDays(30 - i * 3));
            record.setWeight(4.2 + ThreadLocalRandom.current().nextDouble(0, 0.8));
            record.setWeight(Math.round(record.getWeight() * 10.0) / 10.0);
            record.setTemperature(Math.round((37.5 + ThreadLocalRandom.current().nextDouble(0, 1.7)) * 10.0) / 10.0);
            record.setCareType(careTypes[i % careTypes.length]);
            record.setMentalState(i % 7 == 0 ? MentalState.ILL : MentalState.NORMAL);
            healthRecordRepository.save(record);
        }

        Reminder reminder1 = new Reminder();
        reminder1.setPet(pet);
        reminder1.setTitle("今天该喂驱虫药");
        reminder1.setCareType(CareType.DEWORMING);
        reminder1.setReminderDate(today);
        reminder1.setCompleted(false);
        reminder1.setExpired(false);
        reminderRepository.save(reminder1);

        Reminder reminder2 = new Reminder();
        reminder2.setPet(pet);
        reminder2.setTitle("下周该打疫苗");
        reminder2.setCareType(CareType.VACCINE);
        reminder2.setReminderDate(today.plusDays(7));
        reminder2.setCompleted(false);
        reminder2.setExpired(false);
        reminderRepository.save(reminder2);

        Reminder reminder3 = new Reminder();
        reminder3.setPet(pet);
        reminder3.setTitle("上周该洗澡已过期");
        reminder3.setCareType(CareType.BATH);
        reminder3.setReminderDate(today.minusDays(7));
        reminder3.setCompleted(false);
        reminder3.setExpired(true);
        reminderRepository.save(reminder3);
    }

}
