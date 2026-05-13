package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.PetResponse;
import com.petcarebackend.service.PetService;
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
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ApiResponse<List<PetResponse>> getAllPets() {
        return ApiResponse.success("Pets fetched successfully.", petService.getAllPets());
    }

    @GetMapping("/{petId}")
    public ApiResponse<PetResponse> getPetById(@PathVariable Long petId) {
        return ApiResponse.success("Pet fetched successfully.", petService.getPetById(petId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PetResponse>> getPetsByUserId(@PathVariable Long userId) {
        return ApiResponse.success("User pets fetched successfully.", petService.getPetsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PetResponse>> createPet(@RequestBody CreatePetRequest request) {
        PetResponse createdPet = petService.createPet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pet created successfully.", createdPet));
    }
}
