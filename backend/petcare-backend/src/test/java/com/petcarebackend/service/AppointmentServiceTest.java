package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.appointment.AppointmentResponse;
import com.petcarebackend.dto.appointment.CreateAppointmentRequest;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Appointment;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.AppointmentRepository;
import com.petcarebackend.repository.PetRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * [RED → GREEN] AppointmentService için TDD testleri.
 * IAppointmentService arayüzü üzerinden davranış doğrulanır.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Pet samplePet() {
        return new Pet(1L, 10L, "Boncuk", "Kedi", null, "FEMALE",
                LocalDate.of(2022, 3, 10), BigDecimal.valueOf(4.2), null, LocalDateTime.now());
    }

    private Appointment sampleAppointment() {
        return new Appointment(5L, 1L, "Dr. Ahmet", "Hayat Klinigi",
                LocalDateTime.of(2026, 6, 15, 10, 0), "PLANNED", "Rutin kontrol");
    }

    // --- create ---

    @Test
    void createThrowsBadRequestWhenVetNameIsBlank() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                1L, "", "Klinik", LocalDateTime.now().plusDays(1), "PLANNED", null);

        assertThrows(BadRequestException.class, () -> appointmentService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenAppointmentTimeIsNull() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                1L, "Dr. Ahmet", null, null, "PLANNED", null);

        assertThrows(BadRequestException.class, () -> appointmentService.create(request));
    }

    @Test
    void createThrowsBadRequestForInvalidStatus() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                1L, "Dr. Ahmet", null, LocalDateTime.now().plusDays(1), "GECERSIZ", null);

        assertThrows(BadRequestException.class, () -> appointmentService.create(request));
    }

    @Test
    void createDefaultsStatusToPlannedWhenNull() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                1L, "Dr. Ahmet", "Klinik", LocalDateTime.now().plusDays(1), null, null);
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(appointmentRepository.save(any())).thenReturn(5L);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(sampleAppointment()));

        AppointmentResponse result = appointmentService.create(request);

        assertEquals("PLANNED", result.status());
    }

    @Test
    void createThrowsNotFoundWhenPetMissing() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                99L, "Dr. Ahmet", null, LocalDateTime.now().plusDays(1), "PLANNED", null);
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> appointmentService.create(request));
    }

    // --- findByPetId ---

    @Test
    void findByPetIdReturnsAppointments() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(appointmentRepository.findByPetId(1L)).thenReturn(List.of(sampleAppointment()));

        List<AppointmentResponse> result = appointmentService.findByPetId(1L);

        assertEquals(1, result.size());
        assertEquals("Dr. Ahmet", result.get(0).vetName());
    }

    // --- delete ---

    @Test
    void deleteThrowsNotFoundWhenAppointmentMissing() {
        when(appointmentRepository.deleteById(99L)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> appointmentService.delete(99L));
    }
}
