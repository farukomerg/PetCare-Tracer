package com.petcarebackend.service;

import com.petcarebackend.dto.appointment.AppointmentResponse;
import com.petcarebackend.dto.appointment.CreateAppointmentRequest;
import java.util.List;

/**
 * Randevu yönetimi servis sözleşmesi.
 * SOLID — Dependency Inversion Principle (DIP).
 */
public interface IAppointmentService extends CrudService<AppointmentResponse, Long, CreateAppointmentRequest> {

    List<AppointmentResponse> findByPetId(Long petId);

    AppointmentResponse update(Long appointmentId, CreateAppointmentRequest request);
}
