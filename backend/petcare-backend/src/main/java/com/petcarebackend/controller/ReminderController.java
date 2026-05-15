package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.reminder.CreateReminderRequest;
import com.petcarebackend.dto.reminder.ReminderResponse;
import com.petcarebackend.service.ReminderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public ApiResponse<List<ReminderResponse>> getAllReminders() {
        return ApiResponse.success("Reminders fetched successfully.", reminderService.getAllReminders());
    }

    @GetMapping("/{reminderId}")
    public ApiResponse<ReminderResponse> getReminderById(@PathVariable Long reminderId) {
        return ApiResponse.success("Reminder fetched successfully.", reminderService.getReminderById(reminderId));
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<ReminderResponse>> getRemindersByPetId(@PathVariable Long petId) {
        return ApiResponse.success("Pet reminders fetched successfully.", reminderService.getRemindersByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReminderResponse>> createReminder(@RequestBody CreateReminderRequest request) {
        ReminderResponse created = reminderService.createReminder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reminder created successfully.", created));
    }
}
