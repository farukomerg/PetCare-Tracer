package com.petcarebackend.service;

import com.petcarebackend.dto.health.CreateHealthRecordRequest;
import com.petcarebackend.dto.health.HealthRecordResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.HealthRecord;
import com.petcarebackend.repository.HealthRecordRepository;
import com.petcarebackend.repository.PetRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "CHECKUP", "DISEASE", "ALLERGY", "SURGERY", "OTHER"
    );

    private final HealthRecordRepository healthRecordRepository;
    private final PetRepository petRepository;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, PetRepository petRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.petRepository = petRepository;
    }

    public List<HealthRecordResponse> getAllHealthRecords() {
        return healthRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public HealthRecordResponse getHealthRecordById(Long healthRecordId) {
        return healthRecordRepository.findById(healthRecordId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Health record not found: " + healthRecordId));
    }

    public List<HealthRecordResponse> getHealthRecordsByPetId(Long petId) {
        ensurePetExists(petId);
        return healthRecordRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    public HealthRecordResponse createHealthRecord(CreateHealthRecordRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        Long newId = healthRecordRepository.save(normalizeRequest(request));
        return getHealthRecordById(newId);
    }

    public HealthRecordResponse updateHealthRecord(Long healthRecordId, CreateHealthRecordRequest request) {
        if (healthRecordRepository.findById(healthRecordId).isEmpty()) {
            throw new NotFoundException("Health record not found: " + healthRecordId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        healthRecordRepository.update(healthRecordId, normalizeRequest(request));
        return getHealthRecordById(healthRecordId);
    }

    public void deleteHealthRecord(Long healthRecordId) {
        if (healthRecordRepository.deleteById(healthRecordId) == 0) {
            throw new NotFoundException("Health record not found: " + healthRecordId);
        }
    }

    private CreateHealthRecordRequest normalizeRequest(CreateHealthRecordRequest request) {
        return new CreateHealthRecordRequest(
                request.petId(),
                request.recordType().trim().toUpperCase(),
                request.recordDate(),
                request.description().trim()
        );
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void validateRequest(CreateHealthRecordRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (request.recordDate() == null) {
            throw new BadRequestException("recordDate is required.");
        }
        if (request.recordType() == null || request.recordType().isBlank()) {
            throw new BadRequestException("recordType is required.");
        }
        if (!ALLOWED_TYPES.contains(request.recordType().trim().toUpperCase())) {
            throw new BadRequestException("recordType must be one of: CHECKUP, DISEASE, ALLERGY, SURGERY, OTHER");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new BadRequestException("description is required.");
        }
    }

    private HealthRecordResponse toResponse(HealthRecord record) {
        return new HealthRecordResponse(
                record.healthRecordId(),
                record.petId(),
                record.recordType(),
                record.recordDate(),
                record.description(),
                record.createdAt()
        );
    }
}
