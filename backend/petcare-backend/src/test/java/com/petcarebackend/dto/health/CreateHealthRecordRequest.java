package com.petcarebackend.dto.health;

import java.time.LocalDate;

public record CreateHealthRecordRequest(
        Long petId,
        String recordType,
        LocalDate recordDate,
        String description
) {
}
