package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.reminder.CreateReminderRequest;
import com.petcarebackend.dto.reminder.ReminderResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Pet;
import com.petcarebackend.model.Reminder;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.ReminderRepository;
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
 * [RED → GREEN] ReminderService için TDD testleri.
 * IReminderService arayüzü üzerinden davranış doğrulanır.
 */
@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private ReminderService reminderService;

    private Pet samplePet() {
        return new Pet(1L, 10L, "Boncuk", "Kedi", null, "FEMALE",
                LocalDate.of(2022, 3, 10), BigDecimal.valueOf(4.2), null, LocalDateTime.now());
    }

    private Reminder sampleReminder() {
        return new Reminder(3L, 1L, "VACCINE", "Kuduz Asisi Hatirlatmasi",
                LocalDateTime.of(2026, 7, 1, 9, 0), "PENDING", LocalDateTime.now());
    }

    // --- create ---

    @Test
    void createThrowsBadRequestWhenReminderTypeIsBlank() {
        CreateReminderRequest request = new CreateReminderRequest(
                1L, "  ", "Baslik", LocalDateTime.now().plusDays(1), null);

        assertThrows(BadRequestException.class, () -> reminderService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenTitleIsBlank() {
        CreateReminderRequest request = new CreateReminderRequest(
                1L, "VACCINE", "", LocalDateTime.now().plusDays(1), null);

        assertThrows(BadRequestException.class, () -> reminderService.create(request));
    }

    @Test
    void createThrowsBadRequestForInvalidReminderType() {
        CreateReminderRequest request = new CreateReminderRequest(
                1L, "GECERSIZ", "Baslik", LocalDateTime.now().plusDays(1), null);

        assertThrows(BadRequestException.class, () -> reminderService.create(request));
    }

    @Test
    void createDefaultsStatusToPendingWhenNull() {
        CreateReminderRequest request = new CreateReminderRequest(
                1L, "vaccine", "Kuduz Asisi Hatirlatmasi", LocalDateTime.now().plusDays(1), null);
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(reminderRepository.save(any())).thenReturn(3L);
        when(reminderRepository.findById(3L)).thenReturn(Optional.of(sampleReminder()));

        ReminderResponse result = reminderService.create(request);

        assertEquals("PENDING", result.status());
        assertEquals("VACCINE", result.reminderType());
    }

    @Test
    void createNormalizesReminderTypeToUpperCase() {
        CreateReminderRequest request = new CreateReminderRequest(
                1L, "medication", "Ilas Hatirlatmasi", LocalDateTime.now().plusDays(1), null);
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(reminderRepository.save(any())).thenReturn(3L);
        when(reminderRepository.findById(3L)).thenReturn(Optional.of(
                new Reminder(3L, 1L, "MEDICATION", "Ilas Hatirlatmasi",
                        LocalDateTime.now().plusDays(1), "PENDING", LocalDateTime.now())));

        ReminderResponse result = reminderService.create(request);

        assertEquals("MEDICATION", result.reminderType());
    }

    @Test
    void createThrowsNotFoundWhenPetMissing() {
        CreateReminderRequest request = new CreateReminderRequest(
                99L, "VACCINE", "Baslik", LocalDateTime.now().plusDays(1), null);
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reminderService.create(request));
    }

    // --- findByPetId ---

    @Test
    void findByPetIdThrowsNotFoundWhenPetMissing() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reminderService.findByPetId(99L));
    }

    @Test
    void findByPetIdReturnsReminders() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(reminderRepository.findByPetId(1L)).thenReturn(List.of(sampleReminder()));

        List<ReminderResponse> result = reminderService.findByPetId(1L);

        assertEquals(1, result.size());
        assertEquals("VACCINE", result.get(0).reminderType());
    }

    // --- delete ---

    @Test
    void deleteThrowsNotFoundWhenReminderMissing() {
        when(reminderRepository.deleteById(99L)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> reminderService.delete(99L));
    }
}
