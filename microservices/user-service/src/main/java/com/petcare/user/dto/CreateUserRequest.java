package com.petcare.user.dto;

public record CreateUserRequest(String fullName, String email, String password, String phone) {}
