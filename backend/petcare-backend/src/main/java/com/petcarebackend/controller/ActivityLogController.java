package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.activity.ActivityLogResponse;
import com.petcarebackend.dto.activity.CreateActivityLogRequest;
import com.petcarebackend.service.ActivityLogService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ApiResponse<List<ActivityLogResponse>> getAllActivityLogs() {
        return ApiResponse.success("Activity logs fetched successfully.", activityLogService.getAllActivityLogs());
    }

    @GetMapping("/{id}")
    public ApiResponse<ActivityLogResponse> getActivityLogById(@PathVariable String id) {
        return ApiResponse.success("Activity log fetched successfully.", activityLogService.getActivityLogById(id));
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<ActivityLogResponse>> getActivityLogsByPetId(@PathVariable Long petId) {
        return ApiResponse.success("Pet activity logs fetched successfully.", activityLogService.getActivityLogsByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityLogResponse>> createActivityLog(@RequestBody CreateActivityLogRequest request) {
        ActivityLogResponse created = activityLogService.createActivityLog(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Activity log created successfully.", created));
    }

    @PutMapping("/{id}")
    public ApiResponse<ActivityLogResponse> updateActivityLog(
            @PathVariable String id,
            @RequestBody CreateActivityLogRequest request
    ) {
        return ApiResponse.success("Activity log updated successfully.", activityLogService.updateActivityLog(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteActivityLog(@PathVariable String id) {
        activityLogService.deleteActivityLog(id);
        return ApiResponse.success("Activity log deleted successfully.", null);
    }
}
