package com.petcarebackend.dto.health;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HealthRecordResponse(
        Long healthRecordId,
        Long petId,
        String recordType,
        LocalDate recordDate,
        String description,
        LocalDateTime createdAt
) {
}
