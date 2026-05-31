package com.petcare.pet.controller;

import com.petcare.pet.dto.*;
import com.petcare.pet.service.PetService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService svc;

    public PetController(PetService svc) { this.svc = svc; }

    @GetMapping
    public ApiResponse<List<PetResponse>> all() {
        return ApiResponse.success("Pets fetched.", svc.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<PetResponse> byId(@PathVariable Long id) {
        return ApiResponse.success("Pet fetched.", svc.findById(id));
    }

    @GetMapping("/user/{uid}")
    public ApiResponse<List<PetResponse>> byUser(@PathVariable Long uid) {
        return ApiResponse.success("User pets fetched.", svc.findByUserId(uid));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PetResponse>> create(@RequestBody CreatePetRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pet created.", svc.create(r)));
    }

    @PutMapping("/{id}")
    public ApiResponse<PetResponse> update(@PathVariable Long id, @RequestBody UpdatePetRequest r) {
        return ApiResponse.success("Pet updated.", svc.update(id, r));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ApiResponse.success("Pet deleted.", null);
    }
}
