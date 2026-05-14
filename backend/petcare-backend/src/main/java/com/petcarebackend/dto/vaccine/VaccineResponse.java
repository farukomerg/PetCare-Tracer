package com.petcarebackend.dto.vaccine;

public record VaccineResponse(
        Long vaccineId,
        String vaccineName,
        String description,
        Integer repeatDays
) {
}
