package com.petcare.care.dto;
import java.time.LocalDateTime;
public record CreateReminderRequest(Long petId, String reminderType, String title, LocalDateTime remindAt, String status) {}
