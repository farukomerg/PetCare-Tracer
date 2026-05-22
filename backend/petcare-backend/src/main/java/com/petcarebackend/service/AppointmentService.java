package com.petcarebackend.service;

import com.petcarebackend.dto.appointment.AppointmentResponse;
import com.petcarebackend.dto.appointment.CreateAppointmentRequest;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Appointment;
import com.petcarebackend.repository.AppointmentRepository;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.util.ValidationUtils;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService implements IAppointmentService {

    private static final Set<String> ALLOWED_STATUS = Set.of("PLANNED", "COMPLETED", "CANCELLED");

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PetRepository petRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
    }

    @Override
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findAll() kullanınız. */
    @Deprecated
    public List<AppointmentResponse> getAllAppointments() {
        return findAll();
    }

    @Override
    public AppointmentResponse findById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
    }

    /** @deprecated Geriye dönük uyumluluk için; findById() kullanınız. */
    @Deprecated
    public AppointmentResponse getAppointmentById(Long appointmentId) {
        return findById(appointmentId);
    }

    @Override
    public List<AppointmentResponse> findByPetId(Long petId) {
        ensurePetExists(petId);
        return appointmentRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findByPetId() kullanınız. */
    @Deprecated
    public List<AppointmentResponse> getAppointmentsByPetId(Long petId) {
        return findByPetId(petId);
    }

    @Override
    public AppointmentResponse create(CreateAppointmentRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        CreateAppointmentRequest normalized = normalizeRequest(request);
        Long newId = appointmentRepository.save(normalized);
        return findById(newId);
    }

    /** @deprecated Geriye dönük uyumluluk için; create() kullanınız. */
    @Deprecated
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        return create(request);
    }

    @Override
    public AppointmentResponse update(Long appointmentId, CreateAppointmentRequest request) {
        if (appointmentRepository.findById(appointmentId).isEmpty()) {
            throw new NotFoundException("Appointment not found: " + appointmentId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        appointmentRepository.update(appointmentId, normalizeRequest(request));
        return findById(appointmentId);
    }

    /** @deprecated Geriye dönük uyumluluk için; update() kullanınız. */
    @Deprecated
    public AppointmentResponse updateAppointment(Long appointmentId, CreateAppointmentRequest request) {
        return update(appointmentId, request);
    }

    @Override
    public void delete(Long appointmentId) {
        if (appointmentRepository.deleteById(appointmentId) == 0) {
            throw new NotFoundException("Appointment not found: " + appointmentId);
        }
    }

    /** @deprecated Geriye dönük uyumluluk için; delete() kullanınız. */
    @Deprecated
    public void deleteAppointment(Long appointmentId) {
        delete(appointmentId);
    }

    private CreateAppointmentRequest normalizeRequest(CreateAppointmentRequest request) {
        String status = ValidationUtils.isBlank(request.status())
                ? "PLANNED"
                : request.status().trim().toUpperCase();
        return new CreateAppointmentRequest(
                request.petId(),
                request.vetName(),
                request.clinicName(),
                request.appointmentTime(),
                status,
                request.note()
        );
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void validateRequest(CreateAppointmentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (ValidationUtils.isBlank(request.vetName())) {
            throw new BadRequestException("vetName is required.");
        }
        if (request.appointmentTime() == null) {
            throw new BadRequestException("appointmentTime is required.");
        }
        String status = ValidationUtils.isBlank(request.status())
                ? "PLANNED"
                : request.status().trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BadRequestException("status must be one of: PLANNED, COMPLETED, CANCELLED");
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.appointmentId(),
                appointment.petId(),
                appointment.vetName(),
                appointment.clinicName(),
                appointment.appointmentTime(),
                appointment.status(),
                appointment.note()
        );
    }
}
