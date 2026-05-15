package com.petcareadmin.model;

import java.time.LocalDateTime;

public record ReminderItem(
        Long reminderId,
        Long petId,
        String reminderType,
        String title,
        LocalDateTime remindAt,
        String status,
        LocalDateTime createdAt
) {
}
