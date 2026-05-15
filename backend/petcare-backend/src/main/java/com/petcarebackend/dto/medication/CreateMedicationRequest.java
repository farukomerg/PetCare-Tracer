package com.petcarebackend.dto.medication;

public record CreateMedicationRequest(
        String medicationName,
        String form,
        String description
) {
}
