package com.petcarebackend.service;

import com.petcarebackend.dto.medication.CreateMedicationRequest;
import com.petcarebackend.dto.medication.MedicationResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Medication;
import com.petcarebackend.repository.MedicationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    public List<MedicationResponse> getAllMedications() {
        return medicationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicationResponse getMedicationById(Long medicationId) {
        return medicationRepository.findById(medicationId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Medication not found: " + medicationId));
    }

    public MedicationResponse createMedication(CreateMedicationRequest request) {
        validateRequest(request);

        medicationRepository.findByName(request.medicationName().trim())
                .ifPresent(existing -> {
                    throw new BadRequestException("Medication already exists.");
                });

        Long newId = medicationRepository.save(normalizeRequest(request));
        return getMedicationById(newId);
    }

    public MedicationResponse updateMedication(Long medicationId, CreateMedicationRequest request) {
        if (medicationRepository.findById(medicationId).isEmpty()) {
            throw new NotFoundException("Medication not found: " + medicationId);
        }
        validateRequest(request);
        medicationRepository.findByName(request.medicationName().trim())
                .ifPresent(existing -> {
                    if (!existing.medicationId().equals(medicationId)) {
                        throw new BadRequestException("Medication already exists.");
                    }
                });
        medicationRepository.update(medicationId, normalizeRequest(request));
        return getMedicationById(medicationId);
    }

    public void deleteMedication(Long medicationId) {
        if (medicationRepository.deleteById(medicationId) == 0) {
            throw new NotFoundException("Medication not found: " + medicationId);
        }
    }

    private CreateMedicationRequest normalizeRequest(CreateMedicationRequest request) {
        return new CreateMedicationRequest(
                request.medicationName().trim(),
                request.form() != null ? request.form().trim() : null,
                request.description() != null ? request.description().trim() : null
        );
    }

    private void validateRequest(CreateMedicationRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.medicationName() == null || request.medicationName().isBlank()) {
            throw new BadRequestException("medicationName is required.");
        }
    }

    private MedicationResponse toResponse(Medication medication) {
        return new MedicationResponse(
                medication.medicationId(),
                medication.medicationName(),
                medication.form(),
                medication.description()
        );
    }
}
