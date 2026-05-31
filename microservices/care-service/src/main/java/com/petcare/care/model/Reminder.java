package com.petcare.care.model;
import java.time.LocalDateTime;
public record Reminder(Long reminderId, Long petId, String reminderType, String title, LocalDateTime remindAt, String status, LocalDateTime createdAt) {}
