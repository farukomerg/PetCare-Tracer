package com.petcarebackend.model;

import java.time.LocalDate;

public record VaccineRecord(
        Long vaccineRecordId,
        Long petId,
        Long vaccineId,
        LocalDate applicationDate,
        LocalDate nextDueDate,
        String note
) {
}
