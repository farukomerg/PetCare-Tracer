package com.petcarebackend.service;

import com.petcarebackend.dto.activity.ActivityLogResponse;
import com.petcarebackend.dto.activity.CreateActivityLogRequest;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.ActivityLog;
import com.petcarebackend.repository.ActivityLogRepository;
import com.petcarebackend.repository.PetRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private static final Set<String> ALLOWED_TYPES = Set.of("WALK", "PLAY", "SLEEP", "TRAINING", "OTHER");

    private final ActivityLogRepository activityLogRepository;
    private final PetRepository petRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository, PetRepository petRepository) {
        this.activityLogRepository = activityLogRepository;
        this.petRepository = petRepository;
    }

    public List<ActivityLogResponse> getAllActivityLogs() {
        return activityLogRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ActivityLogResponse getActivityLogById(String id) {
        return activityLogRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Activity log not found: " + id));
    }

    public List<ActivityLogResponse> getActivityLogsByPetId(Long petId) {
        ensurePetExists(petId);
        return activityLogRepository.findByPetIdOrderByRecordedAtDesc(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ActivityLogResponse createActivityLog(CreateActivityLogRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());

        ActivityLog activityLog = new ActivityLog();
        activityLog.setPetId(request.petId());
        activityLog.setActivityType(request.activityType().trim().toUpperCase());
        activityLog.setDurationMinutes(request.durationMinutes());
        activityLog.setCaloriesEstimate(request.caloriesEstimate());
        activityLog.setRecordedAt(request.recordedAt() != null ? request.recordedAt() : LocalDateTime.now());
        activityLog.setNote(request.note());

        ActivityLog saved = activityLogRepository.save(activityLog);
        return toResponse(saved);
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void validateRequest(CreateActivityLogRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (request.activityType() == null || request.activityType().isBlank()) {
            throw new BadRequestException("activityType is required.");
        }
        String normalizedType = request.activityType().trim().toUpperCase();
        if (!ALLOWED_TYPES.contains(normalizedType)) {
            throw new BadRequestException("activityType must be one of: WALK, PLAY, SLEEP, TRAINING, OTHER");
        }
        if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
            throw new BadRequestException("durationMinutes must be greater than zero.");
        }
        if (request.caloriesEstimate() != null && request.caloriesEstimate() < 0) {
            throw new BadRequestException("caloriesEstimate cannot be negative.");
        }
    }

    private ActivityLogResponse toResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getPetId(),
                activityLog.getActivityType(),
                activityLog.getDurationMinutes(),
                activityLog.getCaloriesEstimate(),
                activityLog.getRecordedAt(),
                activityLog.getNote()
        );
    }
}
