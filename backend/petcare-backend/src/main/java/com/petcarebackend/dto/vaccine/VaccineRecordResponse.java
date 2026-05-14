package com.petcarebackend.dto.vaccine;

import java.time.LocalDate;

public record VaccineRecordResponse(
        Long vaccineRecordId,
        Long petId,
        Long vaccineId,
        LocalDate applicationDate,
        LocalDate nextDueDate,
        String note
) {
}
