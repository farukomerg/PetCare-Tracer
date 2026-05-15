package com.petcarebackend.model;

public record Medication(
        Long medicationId,
        String medicationName,
        String form,
        String description
) {
}
