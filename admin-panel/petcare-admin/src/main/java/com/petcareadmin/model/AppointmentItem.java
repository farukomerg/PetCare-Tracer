package com.petcareadmin.model;

import java.time.LocalDateTime;

public record AppointmentItem(
        Long appointmentId,
        Long petId,
        String vetName,
        String clinicName,
        LocalDateTime appointmentTime,
        String status,
        String note
) {
}
