package com.petcare.mobile.model;

public class PetResponse {

    private Long petId;
    private Long userId;
    private String petName;
    private String species;
    private String breed;
    private String gender;
    private String birthDate;
    private Double currentWeight;
    private String notes;
    private String createdAt;

    public Long getPetId() {
        return petId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPetName() {
        return petName;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
