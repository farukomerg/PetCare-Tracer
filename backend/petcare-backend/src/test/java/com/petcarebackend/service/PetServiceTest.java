package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.PetResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Pet;
import com.petcarebackend.repository.PetRepository;
import com.petcarebackend.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * [RED → GREEN] PetService için TDD testleri.
 * IPetService arayüzü üzerinden davranış doğrulanır.
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetService petService;

    private Pet samplePet() {
        return new Pet(1L, 10L, "Boncuk", "Kedi", "British Shorthair", "FEMALE",
                LocalDate.of(2022, 3, 10), BigDecimal.valueOf(4.2), "Sakin karakter",
                LocalDateTime.of(2026, 1, 1, 12, 0));
    }

    // --- findAll ---

    @Test
    void findAllReturnsMappedResponses() {
        when(petRepository.findAll()).thenReturn(List.of(samplePet()));

        List<PetResponse> result = petService.findAll();

        assertEquals(1, result.size());
        assertEquals("Boncuk", result.get(0).petName());
    }

    @Test
    void findAllReturnsEmptyListWhenNoPets() {
        when(petRepository.findAll()).thenReturn(List.of());

        List<PetResponse> result = petService.findAll();

        assertEquals(0, result.size());
    }

    // --- findById ---

    @Test
    void findByIdReturnsPetWhenFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));

        PetResponse result = petService.findById(1L);

        assertEquals(1L, result.petId());
        assertEquals("Kedi", result.species());
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> petService.findById(99L));
    }

    // --- create ---

    @Test
    void createThrowsBadRequestWhenPetNameIsBlank() {
        CreatePetRequest request = new CreatePetRequest(10L, " ", "Kedi", null, null, null, null, null);

        assertThrows(BadRequestException.class, () -> petService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenSpeciesIsBlank() {
        CreatePetRequest request = new CreatePetRequest(10L, "Boncuk", "", null, null, null, null, null);

        assertThrows(BadRequestException.class, () -> petService.create(request));
    }

    @Test
    void createThrowsNotFoundWhenUserDoesNotExist() {
        CreatePetRequest request = new CreatePetRequest(10L, "Boncuk", "Kedi", null, null, null, null, null);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> petService.create(request));
    }

    @Test
    void createSavesPetAndReturnsResponse() {
        CreatePetRequest request = new CreatePetRequest(10L, "Boncuk", "Kedi", null, null, null, null, null);
        when(userRepository.findById(10L)).thenReturn(Optional.of(
                new com.petcarebackend.model.User(10L, "Test User", "test@example.com", "hash", null, LocalDateTime.now(), true)));
        when(petRepository.save(any())).thenReturn(1L);
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet()));

        PetResponse result = petService.create(request);

        assertEquals("Boncuk", result.petName());
        verify(petRepository).save(any(CreatePetRequest.class));
    }

    // --- delete ---

    @Test
    void deleteThrowsNotFoundWhenPetMissing() {
        when(petRepository.deleteById(99L)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> petService.delete(99L));
    }

    @Test
    void deleteCallsRepositoryDelete() {
        when(petRepository.deleteById(1L)).thenReturn(1);

        petService.delete(1L);

        verify(petRepository).deleteById(1L);
    }
}
