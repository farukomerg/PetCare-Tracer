package com.petcarebackend.service;

import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.PetResponse;
import com.petcarebackend.dto.pet.UpdatePetRequest;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.UserRepository;
import com.petcarebackend.util.ValidationUtils;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PetService implements IPetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PetResponse> findAll() {
        return petRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findAll() kullanınız. */
    @Deprecated
    public List<PetResponse> getAllPets() {
        return findAll();
    }

    @Override
    public PetResponse findById(Long petId) {
        return petRepository.findById(petId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Pet not found: " + petId));
    }

    /** @deprecated Geriye dönük uyumluluk için; findById() kullanınız. */
    @Deprecated
    public PetResponse getPetById(Long petId) {
        return findById(petId);
    }

    @Override
    public List<PetResponse> findByUserId(Long userId) {
        ensureUserExists(userId);
        return petRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findByUserId() kullanınız. */
    @Deprecated
    public List<PetResponse> getPetsByUserId(Long userId) {
        return findByUserId(userId);
    }

    @Override
    public PetResponse create(CreatePetRequest request) {
        validateCreatePetRequest(request);
        ensureUserExists(request.userId());
        Long petId = petRepository.save(request);
        return findById(petId);
    }

    /** @deprecated Geriye dönük uyumluluk için; create() kullanınız. */
    @Deprecated
    public PetResponse createPet(CreatePetRequest request) {
        return create(request);
    }

    @Override
    public PetResponse update(Long petId, UpdatePetRequest request) {
        if (petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }

        validateUpdatePetRequest(request);
        ensureUserExists(request.userId());
        petRepository.update(petId, request);
        return findById(petId);
    }

    /** @deprecated Geriye dönük uyumluluk için; update() kullanınız. */
    @Deprecated
    public PetResponse updatePet(Long petId, UpdatePetRequest request) {
        return update(petId, request);
    }

    @Override
    public void delete(Long petId) {
        if (petRepository.deleteById(petId) == 0) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    /** @deprecated Geriye dönük uyumluluk için; delete() kullanınız. */
    @Deprecated
    public void deletePet(Long petId) {
        delete(petId);
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
        if (ValidationUtils.isBlank(request.petName())) {
            throw new BadRequestException("petName is required.");
        }
        if (ValidationUtils.isBlank(request.species())) {
            throw new BadRequestException("species is required.");
        }
    }

    private void validateUpdatePetRequest(UpdatePetRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.userId() == null) {
            throw new BadRequestException("userId is required.");
        }
        if (ValidationUtils.isBlank(request.petName())) {
            throw new BadRequestException("petName is required.");
        }
        if (ValidationUtils.isBlank(request.species())) {
            throw new BadRequestException("species is required.");
        }
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
