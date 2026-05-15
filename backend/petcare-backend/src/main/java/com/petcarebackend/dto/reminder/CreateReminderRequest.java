package com.petcarebackend.dto.reminder;

import java.time.LocalDateTime;

public record CreateReminderRequest(
        Long petId,
        String reminderType,
        String title,
        LocalDateTime remindAt,
        String status
) {
}
