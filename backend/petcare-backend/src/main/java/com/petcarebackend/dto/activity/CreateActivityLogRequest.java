package com.petcarebackend.dto.activity;

import java.time.LocalDateTime;

public record CreateActivityLogRequest(
        Long petId,
        String activityType,
        Integer durationMinutes,
        Integer caloriesEstimate,
        LocalDateTime recordedAt,
        String note
) {
}
