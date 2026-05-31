package com.petcare.pet.service;

import com.petcare.pet.dto.*;
import com.petcare.pet.exception.BadRequestException;
import com.petcare.pet.exception.NotFoundException;
import com.petcare.pet.model.Pet;
import com.petcare.pet.repository.PetRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository repo;

    public PetService(PetRepository repo) { this.repo = repo; }

    public List<PetResponse> findAll() {
        return repo.findAll().stream().map(this::toResp).toList();
    }

    public PetResponse findById(Long id) {
        return repo.findById(id).map(this::toResp)
                .orElseThrow(() -> new NotFoundException("Pet not found: " + id));
    }

    public List<PetResponse> findByUserId(Long uid) {
        return repo.findByUserId(uid).stream().map(this::toResp).toList();
    }

    public PetResponse create(CreatePetRequest r) {
        if (r == null || isBlank(r.petName()) || isBlank(r.species()) || r.userId() == null)
            throw new BadRequestException("userId, petName and species are required.");
        return findById(repo.save(r));
    }

    public PetResponse update(Long id, UpdatePetRequest r) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Pet not found: " + id));
        repo.update(id, r);
        return findById(id);
    }

    public void delete(Long id) {
        if (repo.deleteById(id) == 0) throw new NotFoundException("Pet not found: " + id);
    }

    private PetResponse toResp(Pet p) {
        return new PetResponse(p.petId(), p.userId(), p.petName(), p.species(), p.breed(),
                p.gender(), p.birthDate(), p.currentWeight(), p.notes(), p.createdAt());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
