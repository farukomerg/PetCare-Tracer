package com.petcarebackend.service;

import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.dto.vaccine.VaccineResponse;

/**
 * Aşı yönetimi servis sözleşmesi.
 * SOLID — Dependency Inversion Principle (DIP).
 */
public interface IVaccineService extends CrudService<VaccineResponse, Long, CreateVaccineRequest> {

    VaccineResponse update(Long vaccineId, CreateVaccineRequest request);
}
