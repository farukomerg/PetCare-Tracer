package com.petcarebackend.dto.medication;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicationScheduleResponse(
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
