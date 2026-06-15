package com.petcare.care.model;
import java.time.LocalDateTime;
public record Appointment(Long appointmentId, Long petId, Long vetId, String vetName, String clinicName, LocalDateTime appointmentTime, String status, String note) {}
