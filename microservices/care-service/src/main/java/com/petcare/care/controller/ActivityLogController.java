package com.petcare.care.controller;

import com.petcare.care.dto.*;
import com.petcare.care.service.ActivityLogService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService svc;

    public ActivityLogController(ActivityLogService svc) { this.svc = svc; }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<ActivityLogResponse>> byPet(@PathVariable Long petId) {
        return ApiResponse.success("Activity logs fetched.", svc.findByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityLogResponse>> create(@RequestBody CreateActivityLogRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Activity log created.", svc.create(r)));
    }
}
