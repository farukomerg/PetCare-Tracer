package com.petcarebackend.service;

import com.petcarebackend.dto.vaccine.CreateVaccineRecordRequest;
import com.petcarebackend.dto.vaccine.VaccineRecordResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.VaccineRecord;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.VaccineRecordRepository;
import com.petcarebackend.repository.VaccineRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VaccineRecordService {

    private final VaccineRecordRepository vaccineRecordRepository;
    private final PetRepository petRepository;
    private final VaccineRepository vaccineRepository;

    public VaccineRecordService(
            VaccineRecordRepository vaccineRecordRepository,
            PetRepository petRepository,
            VaccineRepository vaccineRepository
    ) {
        this.vaccineRecordRepository = vaccineRecordRepository;
        this.petRepository = petRepository;
        this.vaccineRepository = vaccineRepository;
    }

    public List<VaccineRecordResponse> getAllVaccineRecords() {
        return vaccineRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public VaccineRecordResponse getVaccineRecordById(Long vaccineRecordId) {
        return vaccineRecordRepository.findById(vaccineRecordId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Vaccine record not found: " + vaccineRecordId));
    }

    public List<VaccineRecordResponse> getVaccineRecordsByPetId(Long petId) {
        ensurePetExists(petId);
        return vaccineRecordRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    public VaccineRecordResponse createVaccineRecord(CreateVaccineRecordRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        ensureVaccineExists(request.vaccineId());

        Long newId = vaccineRecordRepository.save(request);
        return getVaccineRecordById(newId);
    }

    public VaccineRecordResponse updateVaccineRecord(Long vaccineRecordId, CreateVaccineRecordRequest request) {
        if (vaccineRecordRepository.findById(vaccineRecordId).isEmpty()) {
            throw new NotFoundException("Vaccine record not found: " + vaccineRecordId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        ensureVaccineExists(request.vaccineId());
        vaccineRecordRepository.update(vaccineRecordId, request);
        return getVaccineRecordById(vaccineRecordId);
    }

    public void deleteVaccineRecord(Long vaccineRecordId) {
        if (vaccineRecordRepository.deleteById(vaccineRecordId) == 0) {
            throw new NotFoundException("Vaccine record not found: " + vaccineRecordId);
        }
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void ensureVaccineExists(Long vaccineId) {
        if (vaccineId == null || vaccineRepository.findById(vaccineId).isEmpty()) {
            throw new NotFoundException("Vaccine not found: " + vaccineId);
        }
    }

    private void validateRequest(CreateVaccineRecordRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (request.vaccineId() == null) {
            throw new BadRequestException("vaccineId is required.");
        }
        if (request.applicationDate() == null) {
            throw new BadRequestException("applicationDate is required.");
        }
    }

    private VaccineRecordResponse toResponse(VaccineRecord record) {
        return new VaccineRecordResponse(
                record.vaccineRecordId(),
                record.petId(),
                record.vaccineId(),
                record.applicationDate(),
                record.nextDueDate(),
                record.note()
        );
    }
}
