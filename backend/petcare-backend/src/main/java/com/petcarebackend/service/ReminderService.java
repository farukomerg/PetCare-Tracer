package com.petcarebackend.service;

import com.petcarebackend.dto.reminder.CreateReminderRequest;
import com.petcarebackend.dto.reminder.ReminderResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Reminder;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.ReminderRepository;
import com.petcarebackend.util.ValidationUtils;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ReminderService implements IReminderService {

    private static final Set<String> ALLOWED_TYPES = Set.of("VACCINE", "MEDICATION", "FEEDING", "APPOINTMENT", "GENERAL");
    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "DONE", "CANCELLED");

    private final ReminderRepository reminderRepository;
    private final PetRepository petRepository;

    public ReminderService(ReminderRepository reminderRepository, PetRepository petRepository) {
        this.reminderRepository = reminderRepository;
        this.petRepository = petRepository;
    }

    @Override
    public List<ReminderResponse> findAll() {
        return reminderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findAll() kullanınız. */
    @Deprecated
    public List<ReminderResponse> getAllReminders() {
        return findAll();
    }

    @Override
    public ReminderResponse findById(Long reminderId) {
        return reminderRepository.findById(reminderId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Reminder not found: " + reminderId));
    }

    /** @deprecated Geriye dönük uyumluluk için; findById() kullanınız. */
    @Deprecated
    public ReminderResponse getReminderById(Long reminderId) {
        return findById(reminderId);
    }

    @Override
    public List<ReminderResponse> findByPetId(Long petId) {
        ensurePetExists(petId);
        return reminderRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findByPetId() kullanınız. */
    @Deprecated
    public List<ReminderResponse> getRemindersByPetId(Long petId) {
        return findByPetId(petId);
    }

    @Override
    public ReminderResponse create(CreateReminderRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        CreateReminderRequest normalized = normalizeRequest(request);
        Long newId = reminderRepository.save(normalized);
        return findById(newId);
    }

    /** @deprecated Geriye dönük uyumluluk için; create() kullanınız. */
    @Deprecated
    public ReminderResponse createReminder(CreateReminderRequest request) {
        return create(request);
    }

    @Override
    public ReminderResponse update(Long reminderId, CreateReminderRequest request) {
        if (reminderRepository.findById(reminderId).isEmpty()) {
            throw new NotFoundException("Reminder not found: " + reminderId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        reminderRepository.update(reminderId, normalizeRequest(request));
        return findById(reminderId);
    }

    /** @deprecated Geriye dönük uyumluluk için; update() kullanınız. */
    @Deprecated
    public ReminderResponse updateReminder(Long reminderId, CreateReminderRequest request) {
        return update(reminderId, request);
    }

    @Override
    public void delete(Long reminderId) {
        if (reminderRepository.deleteById(reminderId) == 0) {
            throw new NotFoundException("Reminder not found: " + reminderId);
        }
    }

    /** @deprecated Geriye dönük uyumluluk için; delete() kullanınız. */
    @Deprecated
    public void deleteReminder(Long reminderId) {
        delete(reminderId);
    }

    private CreateReminderRequest normalizeRequest(CreateReminderRequest request) {
        String type = request.reminderType().trim().toUpperCase();
        String status = ValidationUtils.isBlank(request.status())
                ? "PENDING"
                : request.status().trim().toUpperCase();
        return new CreateReminderRequest(
                request.petId(),
                type,
                request.title(),
                request.remindAt(),
                status
        );
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void validateRequest(CreateReminderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (ValidationUtils.isBlank(request.reminderType())) {
            throw new BadRequestException("reminderType is required.");
        }
        if (ValidationUtils.isBlank(request.title())) {
            throw new BadRequestException("title is required.");
        }
        if (request.remindAt() == null) {
            throw new BadRequestException("remindAt is required.");
        }
        String type = request.reminderType().trim().toUpperCase();
        if (!ALLOWED_TYPES.contains(type)) {
            throw new BadRequestException("reminderType must be one of: VACCINE, MEDICATION, FEEDING, APPOINTMENT, GENERAL");
        }
        String status = ValidationUtils.isBlank(request.status())
                ? "PENDING"
                : request.status().trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BadRequestException("status must be one of: PENDING, DONE, CANCELLED");
        }
    }

    private ReminderResponse toResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.reminderId(),
                reminder.petId(),
                reminder.reminderType(),
                reminder.title(),
                reminder.remindAt(),
                reminder.status(),
                reminder.createdAt()
        );
    }
}
