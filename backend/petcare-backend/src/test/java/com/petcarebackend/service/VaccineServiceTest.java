package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.dto.vaccine.VaccineResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.Vaccine;
import com.petcarebackend.repository.VaccineRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * [RED → GREEN] VaccineService için TDD testleri.
 * IVaccineService arayüzü üzerinden davranış doğrulanır.
 */
@ExtendWith(MockitoExtension.class)
class VaccineServiceTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @InjectMocks
    private VaccineService vaccineService;

    private Vaccine sampleVaccine() {
        return new Vaccine(1L, "Kuduz Asisi", "Yillik tekrar", 365);
    }

    // --- findAll ---

    @Test
    void findAllReturnsMappedVaccines() {
        when(vaccineRepository.findAll()).thenReturn(List.of(sampleVaccine()));

        List<VaccineResponse> result = vaccineService.findAll();

        assertEquals(1, result.size());
        assertEquals("Kuduz Asisi", result.get(0).vaccineName());
    }

    // --- findById ---

    @Test
    void findByIdReturnsVaccineWhenFound() {
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(sampleVaccine()));

        VaccineResponse result = vaccineService.findById(1L);

        assertEquals(1L, result.vaccineId());
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(vaccineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vaccineService.findById(99L));
    }

    // --- create ---

    @Test
    void createThrowsBadRequestWhenVaccineNameIsBlank() {
        CreateVaccineRequest request = new CreateVaccineRequest("  ", null, 365);

        assertThrows(BadRequestException.class, () -> vaccineService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenRepeatDaysIsNegative() {
        CreateVaccineRequest request = new CreateVaccineRequest("Kuduz", null, -1);

        assertThrows(BadRequestException.class, () -> vaccineService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenVaccineAlreadyExists() {
        CreateVaccineRequest request = new CreateVaccineRequest("Kuduz Asisi", null, 365);
        when(vaccineRepository.findByName("Kuduz Asisi")).thenReturn(Optional.of(sampleVaccine()));

        assertThrows(BadRequestException.class, () -> vaccineService.create(request));
    }

    @Test
    void createTrimsNameAndSavesVaccine() {
        CreateVaccineRequest request = new CreateVaccineRequest("  Kuduz Asisi  ", "Aciklama", 365);
        when(vaccineRepository.findByName("Kuduz Asisi")).thenReturn(Optional.empty());
        when(vaccineRepository.save(any())).thenReturn(1L);
        when(vaccineRepository.findById(1L)).thenReturn(Optional.of(sampleVaccine()));

        VaccineResponse result = vaccineService.create(request);

        assertEquals("Kuduz Asisi", result.vaccineName());
    }

    // --- delete ---

    @Test
    void deleteThrowsNotFoundWhenVaccineMissing() {
        when(vaccineRepository.deleteById(99L)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> vaccineService.delete(99L));
    }
}
