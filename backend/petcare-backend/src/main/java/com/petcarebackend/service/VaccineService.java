package com.petcarebackend.service;

import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.dto.vaccine.VaccineResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Vaccine;
import com.petcarebackend.repository.VaccineRepository;
import com.petcarebackend.util.ValidationUtils;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VaccineService implements IVaccineService {

    private final VaccineRepository vaccineRepository;

    public VaccineService(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    public List<VaccineResponse> findAll() {
        return vaccineRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findAll() kullanınız. */
    @Deprecated
    public List<VaccineResponse> getAllVaccines() {
        return findAll();
    }

    @Override
    public VaccineResponse findById(Long vaccineId) {
        return vaccineRepository.findById(vaccineId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Vaccine not found: " + vaccineId));
    }

    /** @deprecated Geriye dönük uyumluluk için; findById() kullanınız. */
    @Deprecated
    public VaccineResponse getVaccineById(Long vaccineId) {
        return findById(vaccineId);
    }

    @Override
    public VaccineResponse create(CreateVaccineRequest request) {
        validateRequest(request);

        vaccineRepository.findByName(request.vaccineName().trim())
                .ifPresent(existing -> {
                    throw new BadRequestException("Vaccine already exists.");
                });

        Long newId = vaccineRepository.save(normalizeRequest(request));
        return findById(newId);
    }

    /** @deprecated Geriye dönük uyumluluk için; create() kullanınız. */
    @Deprecated
    public VaccineResponse createVaccine(CreateVaccineRequest request) {
        return create(request);
    }

    @Override
    public VaccineResponse update(Long vaccineId, CreateVaccineRequest request) {
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
        return findById(vaccineId);
    }

    /** @deprecated Geriye dönük uyumluluk için; update() kullanınız. */
    @Deprecated
    public VaccineResponse updateVaccine(Long vaccineId, CreateVaccineRequest request) {
        return update(vaccineId, request);
    }

    @Override
    public void delete(Long vaccineId) {
        if (vaccineRepository.deleteById(vaccineId) == 0) {
            throw new NotFoundException("Vaccine not found: " + vaccineId);
        }
    }

    /** @deprecated Geriye dönük uyumluluk için; delete() kullanınız. */
    @Deprecated
    public void deleteVaccine(Long vaccineId) {
        delete(vaccineId);
    }

    private CreateVaccineRequest normalizeRequest(CreateVaccineRequest request) {
        return new CreateVaccineRequest(
                request.vaccineName().trim(),
                ValidationUtils.trimOrNull(request.description()),
                request.repeatDays()
        );
    }

    private void validateRequest(CreateVaccineRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (ValidationUtils.isBlank(request.vaccineName())) {
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
