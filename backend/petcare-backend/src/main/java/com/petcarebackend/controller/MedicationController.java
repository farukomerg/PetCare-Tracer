package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.medication.CreateMedicationRequest;
import com.petcarebackend.dto.medication.MedicationResponse;
import com.petcarebackend.service.MedicationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ApiResponse<List<MedicationResponse>> getAllMedications() {
        return ApiResponse.success(
                "Medications fetched successfully.",
                medicationService.getAllMedications()
        );
    }

    @GetMapping("/{medicationId}")
    public ApiResponse<MedicationResponse> getMedicationById(@PathVariable Long medicationId) {
        return ApiResponse.success(
                "Medication fetched successfully.",
                medicationService.getMedicationById(medicationId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @RequestBody CreateMedicationRequest request
    ) {
        MedicationResponse created = medicationService.createMedication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medication created successfully.", created));
    }
}
