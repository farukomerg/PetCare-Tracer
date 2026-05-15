package com.petcarebackend.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
