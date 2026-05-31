package com.petcare.care.model;
import java.time.LocalDateTime;
public record Appointment(Long appointmentId, Long petId, String vetName, String clinicName, LocalDateTime appointmentTime, String status, String note) {}
