package com.petcare.mobile.model;

import com.google.gson.annotations.SerializedName;

public class CreatePetRequest {

    @SerializedName("userId")
    private final long userId;

    @SerializedName("petName")
    private final String petName;

    @SerializedName("species")
    private final String species;

    @SerializedName("breed")
    private final String breed;

    @SerializedName("gender")
    private final String gender;

    @SerializedName("birthDate")
    private final String birthDate;

    @SerializedName("currentWeight")
    private final Double currentWeight;

    @SerializedName("notes")
    private final String notes;

    public CreatePetRequest(long userId, String petName, String species,
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
