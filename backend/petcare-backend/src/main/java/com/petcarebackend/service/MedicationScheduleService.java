package com.petcarebackend.service;

import com.petcarebackend.dto.medication.CreateMedicationScheduleRequest;
import com.petcarebackend.dto.medication.MedicationScheduleResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.MedicationSchedule;
import com.petcarebackend.repository.MedicationRepository;
import com.petcarebackend.repository.MedicationScheduleRepository;
import com.petcarebackend.repository.PetRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MedicationScheduleService {

    private static final Set<String> ALLOWED_STATUS = Set.of("ACTIVE", "COMPLETED", "CANCELLED");

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final PetRepository petRepository;
    private final MedicationRepository medicationRepository;

    public MedicationScheduleService(
            MedicationScheduleRepository medicationScheduleRepository,
            PetRepository petRepository,
            MedicationRepository medicationRepository
    ) {
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.petRepository = petRepository;
        this.medicationRepository = medicationRepository;
    }

    public List<MedicationScheduleResponse> getAllMedicationSchedules() {
        return medicationScheduleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicationScheduleResponse getMedicationScheduleById(Long medicationScheduleId) {
        return medicationScheduleRepository.findById(medicationScheduleId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Medication schedule not found: " + medicationScheduleId));
    }

    public List<MedicationScheduleResponse> getMedicationSchedulesByPetId(Long petId) {
        ensurePetExists(petId);
        return medicationScheduleRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    public MedicationScheduleResponse createMedicationSchedule(CreateMedicationScheduleRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        ensureMedicationExists(request.medicationId());

        Long newId = medicationScheduleRepository.save(normalizeRequest(request));
        return getMedicationScheduleById(newId);
    }

    public MedicationScheduleResponse updateMedicationSchedule(Long medicationScheduleId, CreateMedicationScheduleRequest request) {
        if (medicationScheduleRepository.findById(medicationScheduleId).isEmpty()) {
            throw new NotFoundException("Medication schedule not found: " + medicationScheduleId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        ensureMedicationExists(request.medicationId());
        medicationScheduleRepository.update(medicationScheduleId, normalizeRequest(request));
        return getMedicationScheduleById(medicationScheduleId);
    }

    public void deleteMedicationSchedule(Long medicationScheduleId) {
        if (medicationScheduleRepository.deleteById(medicationScheduleId) == 0) {
            throw new NotFoundException("Medication schedule not found: " + medicationScheduleId);
        }
    }

    private CreateMedicationScheduleRequest normalizeRequest(CreateMedicationScheduleRequest request) {
        String normalizedStatus = request.status() == null || request.status().isBlank()
                ? "ACTIVE"
                : request.status().trim().toUpperCase();

        return new CreateMedicationScheduleRequest(
                request.petId(),
                request.medicationId(),
                request.dosageAmount(),
                request.dosageUnit(),
                request.frequencyPerDay(),
                request.startDate(),
                request.endDate(),
                normalizedStatus,
                request.note()
        );
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void ensureMedicationExists(Long medicationId) {
        if (medicationId == null || medicationRepository.findById(medicationId).isEmpty()) {
            throw new NotFoundException("Medication not found: " + medicationId);
        }
    }

    private void validateRequest(CreateMedicationScheduleRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (request.medicationId() == null) {
            throw new BadRequestException("medicationId is required.");
        }
        if (request.dosageAmount() == null || request.dosageAmount().signum() <= 0) {
            throw new BadRequestException("dosageAmount must be greater than zero.");
        }
        if (request.dosageUnit() == null || request.dosageUnit().isBlank()) {
            throw new BadRequestException("dosageUnit is required.");
        }
        if (request.frequencyPerDay() == null || request.frequencyPerDay() <= 0) {
            throw new BadRequestException("frequencyPerDay must be greater than zero.");
        }
        if (request.startDate() == null) {
            throw new BadRequestException("startDate is required.");
        }
        String status = request.status() == null || request.status().isBlank()
                ? "ACTIVE"
                : request.status().trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BadRequestException("status must be one of: ACTIVE, COMPLETED, CANCELLED");
        }
    }

    private MedicationScheduleResponse toResponse(MedicationSchedule schedule) {
        return new MedicationScheduleResponse(
                schedule.medicationScheduleId(),
                schedule.petId(),
                schedule.medicationId(),
                schedule.dosageAmount(),
                schedule.dosageUnit(),
                schedule.frequencyPerDay(),
                schedule.startDate(),
                schedule.endDate(),
                schedule.status(),
                schedule.note()
        );
    }
}
