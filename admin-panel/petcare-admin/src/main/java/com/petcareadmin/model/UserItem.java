package com.petcareadmin.model;

import java.time.LocalDateTime;

public record UserItem(
        Long userId,
        String fullName,
        String email,
        String phone,
        boolean isActive,
        LocalDateTime createdAt
) {
}
