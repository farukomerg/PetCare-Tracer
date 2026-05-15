package com.petcarebackend.service;

import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.dto.vaccine.VaccineResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Vaccine;
import com.petcarebackend.repository.VaccineRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    public VaccineService(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    public List<VaccineResponse> getAllVaccines() {
        return vaccineRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public VaccineResponse getVaccineById(Long vaccineId) {
        return vaccineRepository.findById(vaccineId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Vaccine not found: " + vaccineId));
    }

    public VaccineResponse createVaccine(CreateVaccineRequest request) {
        validateRequest(request);

        vaccineRepository.findByName(request.vaccineName().trim())
                .ifPresent(existing -> {
                    throw new BadRequestException("Vaccine already exists.");
                });

        Long newId = vaccineRepository.save(normalizeRequest(request));
        return getVaccineById(newId);
    }

    public VaccineResponse updateVaccine(Long vaccineId, CreateVaccineRequest request) {
        if (vaccineRepository.findById(vaccineId).isEmpty()) {
            throw new NotFoundException("Vaccine not found: " + vaccineId);
        }
        validateRequest(request);
        vaccineRepository.findByName(request.vaccineName().trim())
                .ifPresent(existing -> {
                    if (!existing.vaccineId().equals(vaccineId)) {
                        throw new BadRequestException("Vaccine already exists.");
                    }
                });
        vaccineRepository.update(vaccineId, normalizeRequest(request));
        return getVaccineById(vaccineId);
    }

    public void deleteVaccine(Long vaccineId) {
        if (vaccineRepository.deleteById(vaccineId) == 0) {
            throw new NotFoundException("Vaccine not found: " + vaccineId);
        }
    }

    private CreateVaccineRequest normalizeRequest(CreateVaccineRequest request) {
        return new CreateVaccineRequest(
                request.vaccineName().trim(),
                request.description() != null ? request.description().trim() : null,
                request.repeatDays()
        );
    }

    private void validateRequest(CreateVaccineRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.vaccineName() == null || request.vaccineName().isBlank()) {
            throw new BadRequestException("vaccineName is required.");
        }
        if (request.repeatDays() != null && request.repeatDays() < 0) {
            throw new BadRequestException("repeatDays cannot be negative.");
        }
    }

    private VaccineResponse toResponse(Vaccine vaccine) {
        return new VaccineResponse(
                vaccine.vaccineId(),
                vaccine.vaccineName(),
                vaccine.description(),
                vaccine.repeatDays()
        );
    }
}
