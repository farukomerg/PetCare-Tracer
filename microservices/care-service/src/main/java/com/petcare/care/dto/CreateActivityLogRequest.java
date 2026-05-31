package com.petcare.care.dto;
public record CreateActivityLogRequest(Long petId, String activityType, double durationMinutes, String notes) {}
