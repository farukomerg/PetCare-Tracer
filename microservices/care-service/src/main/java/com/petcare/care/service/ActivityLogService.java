package com.petcare.care.service;

import com.petcare.care.dto.*;
import com.petcare.care.exception.BadRequestException;
import com.petcare.care.model.ActivityLog;
import com.petcare.care.repository.ActivityLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final ActivityLogRepository repo;

    public ActivityLogService(ActivityLogRepository repo) { this.repo = repo; }

    public List<ActivityLogResponse> findByPetId(Long petId) {
        return repo.findByPetId(petId).stream().map(this::toResp).toList();
    }

    public ActivityLogResponse create(CreateActivityLogRequest r) {
        if (r == null || r.petId() == null || isBlank(r.activityType()))
            throw new BadRequestException("petId and activityType are required.");
        ActivityLog log = new ActivityLog(r.petId(), r.activityType(), r.durationMinutes(), LocalDateTime.now(), r.notes());
        return toResp(repo.save(log));
    }

    private ActivityLogResponse toResp(ActivityLog l) {
        return new ActivityLogResponse(l.getId(), l.getPetId(), l.getActivityType(),
                l.getDurationMinutes(), l.getLoggedAt(), l.getNotes());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
