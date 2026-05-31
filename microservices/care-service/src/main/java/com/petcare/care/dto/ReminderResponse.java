package com.petcare.care.dto;
import java.time.LocalDateTime;
public record ReminderResponse(Long reminderId, Long petId, String reminderType, String title, LocalDateTime remindAt, String status, LocalDateTime createdAt) {}
