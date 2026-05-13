package com.petcarebackend.dto.user;

public record CreateUserRequest(
        String fullName,
        String email,
        String password,
        String phone
) {
}
