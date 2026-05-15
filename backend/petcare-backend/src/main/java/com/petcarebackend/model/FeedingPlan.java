package com.petcarebackend.model;

import java.math.BigDecimal;

public record FeedingPlan(
        Long feedingPlanId,
        Long petId,
        String foodName,
        BigDecimal amount,
        String amountUnit,
        Integer mealsPerDay,
        String note,
        boolean isActive
) {
}
