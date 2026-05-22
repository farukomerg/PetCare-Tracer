package com.petcarebackend.service;

import com.petcarebackend.dto.reminder.CreateReminderRequest;
import com.petcarebackend.dto.reminder.ReminderResponse;
import java.util.List;

/**
 * Hatırlatma yönetimi servis sözleşmesi.
 * SOLID — Dependency Inversion Principle (DIP).
 */
public interface IReminderService extends CrudService<ReminderResponse, Long, CreateReminderRequest> {

    List<ReminderResponse> findByPetId(Long petId);

    ReminderResponse update(Long reminderId, CreateReminderRequest request);
}
