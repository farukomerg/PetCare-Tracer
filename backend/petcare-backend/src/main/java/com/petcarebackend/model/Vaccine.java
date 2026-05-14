package com.petcarebackend.model;

public record Vaccine(
        Long vaccineId,
        String vaccineName,
        String description,
        Integer repeatDays
) {
}
