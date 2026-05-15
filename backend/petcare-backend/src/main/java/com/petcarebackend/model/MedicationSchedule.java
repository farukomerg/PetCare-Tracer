package com.petcarebackend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicationSchedule(
        Long medicationScheduleId,
        Long petId,
        Long medicationId,
        BigDecimal dosageAmount,
        String dosageUnit,
        Integer frequencyPerDay,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String note
) {
}
