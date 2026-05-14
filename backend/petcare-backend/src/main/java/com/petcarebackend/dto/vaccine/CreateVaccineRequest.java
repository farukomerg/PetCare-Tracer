package com.petcarebackend.dto.vaccine;

public record CreateVaccineRequest(
        String vaccineName,
        String description,
        Integer repeatDays
) {
}
