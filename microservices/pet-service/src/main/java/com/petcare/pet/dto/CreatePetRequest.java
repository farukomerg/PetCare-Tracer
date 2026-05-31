package com.petcare.pet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePetRequest(
        Long userId, String petName, String species, String breed,
        String gender, LocalDate birthDate, BigDecimal currentWeight, String notes
) {}
