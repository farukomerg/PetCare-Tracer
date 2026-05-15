package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.activity.ActivityLogResponse;
import com.petcarebackend.dto.activity.CreateActivityLogRequest;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.model.ActivityLog;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.ActivityLogRepository;
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
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    @Test
    void createActivityLogNormalizesTypeAndDefaultsRecordedAt() {
        Pet pet = new Pet(1L, 1L, "Boncuk", "Kedi", "British", "FEMALE", LocalDate.of(2022, 3, 10),
                BigDecimal.valueOf(4.2), "Sakin", LocalDateTime.now());

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(activityLogRepository.save(any(ActivityLog.class))).thenAnswer(invocation -> {
            ActivityLog log = invocation.getArgument(0);
            log.setId("mongo-1");
            return log;
        });

        ActivityLogResponse response = activityLogService.createActivityLog(
                new CreateActivityLogRequest(1L, "walk", 35, 120, null, "aksam")
        );

        assertEquals("mongo-1", response.id());
        assertEquals("WALK", response.activityType());
        assertNotNull(response.recordedAt());
    }

    @Test
    void createActivityLogThrowsForInvalidActivityType() {
        assertThrows(BadRequestException.class,
                () -> activityLogService.createActivityLog(
                        new CreateActivityLogRequest(1L, "INVALID", 10, 5, null, null)
                ));
    }
}
