package com.petcare.care.service;

import com.petcare.care.dto.*;
import com.petcare.care.exception.BadRequestException;
import com.petcare.care.exception.NotFoundException;
import com.petcare.care.model.Reminder;
import com.petcare.care.repository.ReminderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    private final ReminderRepository repo;

    public ReminderService(ReminderRepository repo) { this.repo = repo; }

    public List<ReminderResponse> findAll() {
        return repo.findAll().stream().map(this::toResp).toList();
    }

    public List<ReminderResponse> findByPetId(Long petId) {
        return repo.findByPetId(petId).stream().map(this::toResp).toList();
    }

    public ReminderResponse create(CreateReminderRequest r) {
        if (r == null || r.petId() == null || isBlank(r.title()))
            throw new BadRequestException("petId and title are required.");
        Long id = repo.save(r);
        return repo.findById(id).map(this::toResp).orElseThrow();
    }

    public void delete(Long id) {
        if (repo.deleteById(id) == 0) throw new NotFoundException("Reminder not found: " + id);
    }

    private ReminderResponse toResp(Reminder r) {
        return new ReminderResponse(r.reminderId(), r.petId(), r.reminderType(),
                r.title(), r.remindAt(), r.status(), r.createdAt());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
