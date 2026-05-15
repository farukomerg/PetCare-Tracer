package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.feeding.CreateFeedingPlanRequest;
import com.petcarebackend.dto.feeding.FeedingPlanResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.model.FeedingPlan;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.FeedingPlanRepository;
import com.petcarebackend.repository.PetRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedingPlanServiceTest {

    @Mock
    private FeedingPlanRepository feedingPlanRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private FeedingPlanService feedingPlanService;

    @Test
    void createFeedingPlanDefaultsIsActiveToTrue() {
        Pet pet = new Pet(1L, 1L, "Boncuk", "Kedi", "British", "FEMALE", LocalDate.of(2022, 3, 10),
                BigDecimal.valueOf(4.2), "Sakin", LocalDateTime.now());
        FeedingPlan feedingPlan = new FeedingPlan(15L, 1L, "Tavuklu Mama", BigDecimal.valueOf(80),
                "gram", 2, "Sabah ve aksam", true);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(feedingPlanRepository.save(any(CreateFeedingPlanRequest.class))).thenReturn(15L);
        when(feedingPlanRepository.findById(15L)).thenReturn(Optional.of(feedingPlan));

        FeedingPlanResponse response = feedingPlanService.createFeedingPlan(
                new CreateFeedingPlanRequest(1L, " Tavuklu Mama ", BigDecimal.valueOf(80), " gram ", 2,
                        "Sabah ve aksam", null)
        );

        assertEquals(15L, response.feedingPlanId());
        assertEquals(true, response.isActive());
    }

    @Test
    void createFeedingPlanThrowsForInvalidMealCount() {
        assertThrows(BadRequestException.class,
                () -> feedingPlanService.createFeedingPlan(
                        new CreateFeedingPlanRequest(1L, "Mama", BigDecimal.TEN, "gram", 0, null, true)
                ));
    }
}
