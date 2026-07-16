package com.petcare.scheduler;

import com.petcare.entity.Reminder;
import com.petcare.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;

    @Scheduled(cron = "0 0 6 * * ?")
    public void scanExpiredReminders() {
        log.info("开始扫描过期提醒...");
        List<Reminder> expiredReminders = reminderRepository.findByCompletedFalseAndReminderDateBefore(LocalDate.now());
        for (Reminder reminder : expiredReminders) {
            reminder.setExpired(true);
        }
        reminderRepository.saveAll(expiredReminders);
        log.info("扫描完成，共处理 {} 条过期提醒", expiredReminders.size());
    }

}
