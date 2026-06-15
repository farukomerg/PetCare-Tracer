package com.petcare.care.dto;
import java.time.LocalDateTime;
public record CreateAppointmentRequest(Long petId, Long vetId, String vetName, String clinicName, LocalDateTime appointmentTime, String status, String note) {}
