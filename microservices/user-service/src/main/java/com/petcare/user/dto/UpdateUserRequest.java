package com.petcare.user.dto;

public record UpdateUserRequest(String fullName, String email, String phone, Boolean isActive) {}
