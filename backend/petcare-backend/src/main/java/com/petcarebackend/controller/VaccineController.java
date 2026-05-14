package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.dto.vaccine.VaccineResponse;
import com.petcarebackend.service.VaccineService;
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
@RequestMapping("/api/vaccines")
public class VaccineController {

    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public ApiResponse<List<VaccineResponse>> getAllVaccines() {
        return ApiResponse.success(
                "Vaccines fetched successfully.",
                vaccineService.getAllVaccines()
        );
    }

    @GetMapping("/{vaccineId}")
    public ApiResponse<VaccineResponse> getVaccineById(@PathVariable Long vaccineId) {
        return ApiResponse.success(
                "Vaccine fetched successfully.",
                vaccineService.getVaccineById(vaccineId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VaccineResponse>> createVaccine(@RequestBody CreateVaccineRequest request) {
        VaccineResponse created = vaccineService.createVaccine(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vaccine created successfully.", created));
    }
}
