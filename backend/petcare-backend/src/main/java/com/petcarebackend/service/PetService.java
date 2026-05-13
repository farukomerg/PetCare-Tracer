package com.petcarebackend.service;

import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.PetResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<PetResponse> getAllPets() {
        return petRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PetResponse getPetById(Long petId) {
        return petRepository.findById(petId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Pet not found: " + petId));
    }

    public List<PetResponse> getPetsByUserId(Long userId) {
        ensureUserExists(userId);
        return petRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public PetResponse createPet(CreatePetRequest request) {
        validateCreatePetRequest(request);
        ensureUserExists(request.userId());
        Long petId = petRepository.save(request);
        return getPetById(petId);
    }

    private void ensureUserExists(Long userId) {
        if (userId == null || userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private void validateCreatePetRequest(CreatePetRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.userId() == null) {
            throw new BadRequestException("userId is required.");
        }
        if (isBlank(request.petName())) {
            throw new BadRequestException("petName is required.");
        }
        if (isBlank(request.species())) {
            throw new BadRequestException("species is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.petId(),
                pet.userId(),
                pet.petName(),
                pet.species(),
                pet.breed(),
                pet.gender(),
                pet.birthDate(),
                pet.currentWeight(),
                pet.notes(),
                pet.createdAt()
        );
    }
}
