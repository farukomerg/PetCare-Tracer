package com.petcare.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        Boolean isActive,
        LocalDateTime createdAt
) {}
