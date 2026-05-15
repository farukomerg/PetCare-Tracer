package com.petcarebackend.dto.feeding;

import java.math.BigDecimal;

public record CreateFeedingPlanRequest(
        Long petId,
        String foodName,
        BigDecimal amount,
        String amountUnit,
        Integer mealsPerDay,
        String note,
        Boolean isActive
) {
}
