package com.petcare.mobile.model;

import java.math.BigDecimal;

public class UpdatePetRequest {
    private final Long userId;
    private final String petName;
    private final String species;
    private final String breed;
    private final String gender;
    private final String birthDate;
    private final Double currentWeight;
    private final String notes;

    public UpdatePetRequest(Long userId, String petName, String species,
                            String breed, String gender, String birthDate,
                            Double currentWeight, String notes) {
        this.userId = userId;
        this.petName = petName;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.birthDate = birthDate;
        this.currentWeight = currentWeight;
        this.notes = notes;
    }
}
