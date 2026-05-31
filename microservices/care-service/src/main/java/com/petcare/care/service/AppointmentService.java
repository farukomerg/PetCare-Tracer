package com.petcare.care.service;

import com.petcare.care.dto.*;
import com.petcare.care.exception.BadRequestException;
import com.petcare.care.exception.NotFoundException;
import com.petcare.care.model.Appointment;
import com.petcare.care.repository.AppointmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) { this.repo = repo; }

    public List<AppointmentResponse> findAll() {
        return repo.findAll().stream().map(this::toResp).toList();
    }

    public AppointmentResponse findById(Long id) {
        return repo.findById(id).map(this::toResp)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + id));
    }

    public List<AppointmentResponse> findByPetId(Long petId) {
        return repo.findByPetId(petId).stream().map(this::toResp).toList();
    }

    public AppointmentResponse create(CreateAppointmentRequest r) {
        if (r == null || r.petId() == null || isBlank(r.vetName()) || r.appointmentTime() == null)
            throw new BadRequestException("petId, vetName and appointmentTime are required.");
        return findById(repo.save(r));
    }

    public void delete(Long id) {
        if (repo.deleteById(id) == 0) throw new NotFoundException("Appointment not found: " + id);
    }

    private AppointmentResponse toResp(Appointment a) {
        return new AppointmentResponse(a.appointmentId(), a.petId(), a.vetName(),
                a.clinicName(), a.appointmentTime(), a.status(), a.note());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
