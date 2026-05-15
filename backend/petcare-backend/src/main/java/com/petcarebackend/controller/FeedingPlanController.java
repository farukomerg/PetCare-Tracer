package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.feeding.CreateFeedingPlanRequest;
import com.petcarebackend.dto.feeding.FeedingPlanResponse;
import com.petcarebackend.service.FeedingPlanService;
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
@RequestMapping("/api/feeding-plans")
public class FeedingPlanController {

    private final FeedingPlanService feedingPlanService;

    public FeedingPlanController(FeedingPlanService feedingPlanService) {
        this.feedingPlanService = feedingPlanService;
    }

    @GetMapping
    public ApiResponse<List<FeedingPlanResponse>> getAllFeedingPlans() {
        return ApiResponse.success("Feeding plans fetched successfully.", feedingPlanService.getAllFeedingPlans());
    }

    @GetMapping("/{feedingPlanId}")
    public ApiResponse<FeedingPlanResponse> getFeedingPlanById(@PathVariable Long feedingPlanId) {
        return ApiResponse.success("Feeding plan fetched successfully.", feedingPlanService.getFeedingPlanById(feedingPlanId));
    }

    @GetMapping("/pet/{petId}")
    public ApiResponse<List<FeedingPlanResponse>> getFeedingPlansByPetId(@PathVariable Long petId) {
        return ApiResponse.success("Pet feeding plans fetched successfully.", feedingPlanService.getFeedingPlansByPetId(petId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedingPlanResponse>> createFeedingPlan(@RequestBody CreateFeedingPlanRequest request) {
        FeedingPlanResponse created = feedingPlanService.createFeedingPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feeding plan created successfully.", created));
    }
}
