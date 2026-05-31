package com.petcare.care.controller;

import com.petcare.care.dto.*;
import com.petcare.care.service.ReminderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService svc;

    public ReminderController(ReminderService svc) { this.svc = svc; }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<ReminderResponse>> byPet(@PathVariable Long petId) {
        return ApiResponse.success("Reminders fetched.", svc.findByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReminderResponse>> create(@RequestBody CreateReminderRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reminder created.", svc.create(r)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ApiResponse.success("Reminder deleted.", null);
    }
}
