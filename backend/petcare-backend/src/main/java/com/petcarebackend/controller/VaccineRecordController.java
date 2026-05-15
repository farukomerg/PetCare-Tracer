package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.vaccine.CreateVaccineRecordRequest;
import com.petcarebackend.dto.vaccine.VaccineRecordResponse;
import com.petcarebackend.service.VaccineRecordService;
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
@RequestMapping("/api/vaccine-records")
public class VaccineRecordController {

    private final VaccineRecordService vaccineRecordService;

    public VaccineRecordController(VaccineRecordService vaccineRecordService) {
        this.vaccineRecordService = vaccineRecordService;
    }

    @GetMapping
    public ApiResponse<List<VaccineRecordResponse>> getAllVaccineRecords() {
        return ApiResponse.success(
                "Vaccine records fetched successfully.",
                vaccineRecordService.getAllVaccineRecords()
        );
    }

    @GetMapping("/{vaccineRecordId}")
    public ApiResponse<VaccineRecordResponse> getVaccineRecordById(@PathVariable Long vaccineRecordId) {
        return ApiResponse.success(
                "Vaccine record fetched successfully.",
                vaccineRecordService.getVaccineRecordById(vaccineRecordId)
        );
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<VaccineRecordResponse>> getVaccineRecordsByPetId(@PathVariable Long petId) {
        return ApiResponse.success(
                "Pet vaccine records fetched successfully.",
                vaccineRecordService.getVaccineRecordsByPetId(petId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VaccineRecordResponse>> createVaccineRecord(
            @RequestBody CreateVaccineRecordRequest request
    ) {
        VaccineRecordResponse created = vaccineRecordService.createVaccineRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vaccine record created successfully.", created));
    }

    @PutMapping("/{vaccineRecordId}")
    public ApiResponse<VaccineRecordResponse> updateVaccineRecord(
            @PathVariable Long vaccineRecordId,
            @RequestBody CreateVaccineRecordRequest request
    ) {
        return ApiResponse.success(
                "Vaccine record updated successfully.",
                vaccineRecordService.updateVaccineRecord(vaccineRecordId, request)
        );
    }

    @DeleteMapping("/{vaccineRecordId}")
    public ApiResponse<Void> deleteVaccineRecord(@PathVariable Long vaccineRecordId) {
        vaccineRecordService.deleteVaccineRecord(vaccineRecordId);
        return ApiResponse.success("Vaccine record deleted successfully.", null);
    }
}
