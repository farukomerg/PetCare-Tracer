package com.petcare.care.controller;

import com.petcare.care.dto.*;
import com.petcare.care.service.AppointmentService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService svc;

    public AppointmentController(AppointmentService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<List<AppointmentResponse>> all() {
        return ApiResponse.success("Appointments fetched.", svc.findAll());
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<AppointmentResponse>> byPet(@PathVariable Long petId) {
        return ApiResponse.success("Pet appointments fetched.", svc.findByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@RequestBody CreateAppointmentRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created.", svc.create(r)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ApiResponse.success("Appointment deleted.", null);
    }
}
