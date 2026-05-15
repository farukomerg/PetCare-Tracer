package com.petcarebackend.dto.appointment;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        Long petId,
        String vetName,
        String clinicName,
        LocalDateTime appointmentTime,
        String status,
        String note
) {
}
