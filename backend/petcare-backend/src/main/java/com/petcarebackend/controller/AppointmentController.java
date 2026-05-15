package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.appointment.AppointmentResponse;
import com.petcarebackend.dto.appointment.CreateAppointmentRequest;
import com.petcarebackend.service.AppointmentService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ApiResponse<List<AppointmentResponse>> getAllAppointments() {
        return ApiResponse.success("Appointments fetched successfully.", appointmentService.getAllAppointments());
    }

    @GetMapping("/{appointmentId}")
    public ApiResponse<AppointmentResponse> getAppointmentById(@PathVariable Long appointmentId) {
        return ApiResponse.success("Appointment fetched successfully.", appointmentService.getAppointmentById(appointmentId));
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<AppointmentResponse>> getAppointmentsByPetId(@PathVariable Long petId) {
        return ApiResponse.success("Pet appointments fetched successfully.", appointmentService.getAppointmentsByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(@RequestBody CreateAppointmentRequest request) {
        AppointmentResponse created = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created successfully.", created));
    }

    @PutMapping("/{appointmentId}")
    public ApiResponse<AppointmentResponse> updateAppointment(
            @PathVariable Long appointmentId,
            @RequestBody CreateAppointmentRequest request
    ) {
        return ApiResponse.success("Appointment updated successfully.", appointmentService.updateAppointment(appointmentId, request));
    }

    @DeleteMapping("/{appointmentId}")
    public ApiResponse<Void> deleteAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ApiResponse.success("Appointment deleted successfully.", null);
    }
}
