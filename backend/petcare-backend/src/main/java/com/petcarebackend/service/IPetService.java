package com.petcarebackend.service;

import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.PetResponse;
import com.petcarebackend.dto.pet.UpdatePetRequest;
import java.util.List;

/**
 * Evcil hayvan yönetimi servis sözleşmesi.
 * SOLID — Dependency Inversion Principle (DIP) gereği somut sınıf yerine bu
 * arayüze bağımlılık kurulmalıdır.
 */
public interface IPetService extends CrudService<PetResponse, Long, CreatePetRequest> {

    List<PetResponse> findByUserId(Long userId);

    PetResponse update(Long petId, UpdatePetRequest request);
}
