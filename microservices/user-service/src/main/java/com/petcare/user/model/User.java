package com.petcare.user.model;

import java.time.LocalDateTime;

public record User(
        Long userId,
        String fullName,
        String email,
        String passwordHash,
        String phone,
        LocalDateTime createdAt,
        Boolean isActive
) {}
