package com.petcarebackend.dto.medication;

public record MedicationResponse(
        Long medicationId,
        String medicationName,
        String form,
        String description
) {
}
