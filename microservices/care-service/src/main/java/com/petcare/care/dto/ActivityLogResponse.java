package com.petcare.care.dto;
import java.time.LocalDateTime;
public record ActivityLogResponse(String id, Long petId, String activityType, double durationMinutes, LocalDateTime loggedAt, String notes) {}
