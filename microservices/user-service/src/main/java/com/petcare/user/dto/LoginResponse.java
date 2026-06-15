package com.petcare.user.dto;

import java.time.LocalDateTime;

public record LoginResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        Boolean isActive,
        LocalDateTime createdAt,
        String userRole
) {}
