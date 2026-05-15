package com.petcarebackend.dto.user;

public record UpdateUserRequest(
        String fullName,
        String email,
        String phone,
        Boolean isActive
) {
}
