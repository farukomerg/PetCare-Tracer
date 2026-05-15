package com.petcarebackend.service;

import com.petcarebackend.dto.feeding.CreateFeedingPlanRequest;
import com.petcarebackend.dto.feeding.FeedingPlanResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.FeedingPlan;
import com.petcarebackend.repository.FeedingPlanRepository;
import com.petcarebackend.repository.PetRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FeedingPlanService {

    private final FeedingPlanRepository feedingPlanRepository;
    private final PetRepository petRepository;

    public FeedingPlanService(FeedingPlanRepository feedingPlanRepository, PetRepository petRepository) {
        this.feedingPlanRepository = feedingPlanRepository;
        this.petRepository = petRepository;
    }

    public List<FeedingPlanResponse> getAllFeedingPlans() {
        return feedingPlanRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FeedingPlanResponse getFeedingPlanById(Long feedingPlanId) {
        return feedingPlanRepository.findById(feedingPlanId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Feeding plan not found: " + feedingPlanId));
    }

    public List<FeedingPlanResponse> getFeedingPlansByPetId(Long petId) {
        ensurePetExists(petId);
        return feedingPlanRepository.findByPetId(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    public FeedingPlanResponse createFeedingPlan(CreateFeedingPlanRequest request) {
        validateRequest(request);
        ensurePetExists(request.petId());
        CreateFeedingPlanRequest normalized = normalizeRequest(request);
        Long newId = feedingPlanRepository.save(normalized);
        return getFeedingPlanById(newId);
    }

    public FeedingPlanResponse updateFeedingPlan(Long feedingPlanId, CreateFeedingPlanRequest request) {
        if (feedingPlanRepository.findById(feedingPlanId).isEmpty()) {
            throw new NotFoundException("Feeding plan not found: " + feedingPlanId);
        }
        validateRequest(request);
        ensurePetExists(request.petId());
        feedingPlanRepository.update(feedingPlanId, normalizeRequest(request));
        return getFeedingPlanById(feedingPlanId);
    }

    public void deleteFeedingPlan(Long feedingPlanId) {
        if (feedingPlanRepository.deleteById(feedingPlanId) == 0) {
            throw new NotFoundException("Feeding plan not found: " + feedingPlanId);
        }
    }

    private CreateFeedingPlanRequest normalizeRequest(CreateFeedingPlanRequest request) {
        return new CreateFeedingPlanRequest(
                request.petId(),
                request.foodName().trim(),
                request.amount(),
                request.amountUnit().trim(),
                request.mealsPerDay(),
                request.note(),
                request.isActive() == null ? Boolean.TRUE : request.isActive()
        );
    }

    private void ensurePetExists(Long petId) {
        if (petId == null || petRepository.findById(petId).isEmpty()) {
            throw new NotFoundException("Pet not found: " + petId);
        }
    }

    private void validateRequest(CreateFeedingPlanRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.petId() == null) {
            throw new BadRequestException("petId is required.");
        }
        if (request.foodName() == null || request.foodName().isBlank()) {
            throw new BadRequestException("foodName is required.");
        }
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new BadRequestException("amount must be greater than zero.");
        }
        if (request.amountUnit() == null || request.amountUnit().isBlank()) {
            throw new BadRequestException("amountUnit is required.");
        }
        if (request.mealsPerDay() == null || request.mealsPerDay() <= 0) {
            throw new BadRequestException("mealsPerDay must be greater than zero.");
        }
    }

    private FeedingPlanResponse toResponse(FeedingPlan feedingPlan) {
        return new FeedingPlanResponse(
                feedingPlan.feedingPlanId(),
                feedingPlan.petId(),
                feedingPlan.foodName(),
                feedingPlan.amount(),
                feedingPlan.amountUnit(),
                feedingPlan.mealsPerDay(),
                feedingPlan.note(),
                feedingPlan.isActive()
        );
    }
}
