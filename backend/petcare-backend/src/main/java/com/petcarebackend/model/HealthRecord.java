package com.petcarebackend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HealthRecord(
        Long healthRecordId,
        Long petId,
        String recordType,
        LocalDate recordDate,
        String description,
        LocalDateTime createdAt
) {
}
