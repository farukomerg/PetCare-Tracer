package com.petcarebackend.dto.user;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        boolean isActive,
        LocalDateTime createdAt
) {
}
