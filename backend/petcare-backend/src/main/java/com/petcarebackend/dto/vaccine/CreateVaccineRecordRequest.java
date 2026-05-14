package com.petcarebackend.dto.vaccine;

import java.time.LocalDate;

public record CreateVaccineRecordRequest(
        Long petId,
        Long vaccineId,
        LocalDate applicationDate,
        LocalDate nextDueDate,
        String note
) {
}
