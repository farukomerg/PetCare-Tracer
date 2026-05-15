package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.medication.CreateMedicationScheduleRequest;
import com.petcarebackend.dto.medication.MedicationScheduleResponse;
import com.petcarebackend.service.MedicationScheduleService;
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
@RequestMapping("/api/medication-schedules")
public class MedicationScheduleController {

    private final MedicationScheduleService medicationScheduleService;

    public MedicationScheduleController(MedicationScheduleService medicationScheduleService) {
        this.medicationScheduleService = medicationScheduleService;
    }

    @GetMapping
    public ApiResponse<List<MedicationScheduleResponse>> getAllMedicationSchedules() {
        return ApiResponse.success(
                "Medication schedules fetched successfully.",
                medicationScheduleService.getAllMedicationSchedules()
        );
    }

    @GetMapping("/{medicationScheduleId}")
    public ApiResponse<MedicationScheduleResponse> getMedicationScheduleById(
            @PathVariable Long medicationScheduleId
    ) {
        return ApiResponse.success(
                "Medication schedule fetched successfully.",
                medicationScheduleService.getMedicationScheduleById(medicationScheduleId)
        );
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<MedicationScheduleResponse>> getMedicationSchedulesByPetId(
            @PathVariable Long petId
    ) {
        return ApiResponse.success(
                "Pet medication schedules fetched successfully.",
                medicationScheduleService.getMedicationSchedulesByPetId(petId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicationScheduleResponse>> createMedicationSchedule(
            @RequestBody CreateMedicationScheduleRequest request
    ) {
        MedicationScheduleResponse created = medicationScheduleService.createMedicationSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medication schedule created successfully.", created));
    }

    @PutMapping("/{medicationScheduleId}")
    public ApiResponse<MedicationScheduleResponse> updateMedicationSchedule(
            @PathVariable Long medicationScheduleId,
            @RequestBody CreateMedicationScheduleRequest request
    ) {
        return ApiResponse.success(
                "Medication schedule updated successfully.",
                medicationScheduleService.updateMedicationSchedule(medicationScheduleId, request)
        );
    }

    @DeleteMapping("/{medicationScheduleId}")
    public ApiResponse<Void> deleteMedicationSchedule(@PathVariable Long medicationScheduleId) {
        medicationScheduleService.deleteMedicationSchedule(medicationScheduleId);
        return ApiResponse.success("Medication schedule deleted successfully.", null);
    }
}
