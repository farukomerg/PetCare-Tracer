package com.petcarebackend.dto.pet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PetResponse(
        Long petId,
        Long userId,
        String petName,
        String species,
        String breed,
        String gender,
        LocalDate birthDate,
        BigDecimal currentWeight,
        String notes,
        LocalDateTime createdAt
) {
}
