package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.health.CreateHealthRecordRequest;
import com.petcarebackend.dto.health.HealthRecordResponse;
import com.petcarebackend.service.HealthRecordService;
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
@RequestMapping("/api/health-records")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @GetMapping
    public ApiResponse<List<HealthRecordResponse>> getAllHealthRecords() {
        return ApiResponse.success(
                "Health records fetched successfully.",
                healthRecordService.getAllHealthRecords()
        );
    }

    @GetMapping("/{healthRecordId}")
    public ApiResponse<HealthRecordResponse> getHealthRecordById(@PathVariable Long healthRecordId) {
        return ApiResponse.success(
                "Health record fetched successfully.",
                healthRecordService.getHealthRecordById(healthRecordId)
        );
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<HealthRecordResponse>> getHealthRecordsByPetId(@PathVariable Long petId) {
        return ApiResponse.success(
                "Pet health records fetched successfully.",
                healthRecordService.getHealthRecordsByPetId(petId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HealthRecordResponse>> createHealthRecord(
            @RequestBody CreateHealthRecordRequest request
    ) {
        HealthRecordResponse created = healthRecordService.createHealthRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Health record created successfully.", created));
    }

    @PutMapping("/{healthRecordId}")
    public ApiResponse<HealthRecordResponse> updateHealthRecord(
            @PathVariable Long healthRecordId,
            @RequestBody CreateHealthRecordRequest request
    ) {
        return ApiResponse.success("Health record updated successfully.", healthRecordService.updateHealthRecord(healthRecordId, request));
    }

    @DeleteMapping("/{healthRecordId}")
    public ApiResponse<Void> deleteHealthRecord(@PathVariable Long healthRecordId) {
        healthRecordService.deleteHealthRecord(healthRecordId);
        return ApiResponse.success("Health record deleted successfully.", null);
    }
}
