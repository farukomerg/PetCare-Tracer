package com.petcare.user.service;

import com.petcare.user.dto.*;
import com.petcare.user.exception.BadRequestException;
import com.petcare.user.exception.NotFoundException;
import com.petcare.user.model.User;
import com.petcare.user.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    public UserResponse create(CreateUserRequest req) {
        if (req == null || isBlank(req.fullName()) || isBlank(req.email()) || isBlank(req.password()))
            throw new BadRequestException("fullName, email and password are required.");
        userRepository.findByEmail(req.email())
                .ifPresent(u -> { throw new BadRequestException("Email already registered."); });
        String hash = passwordEncoder.encode(req.password().trim());
        Long id = userRepository.save(req, hash);
        return findById(id);
    }

    public UserResponse update(Long id, UpdateUserRequest req) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (req == null || isBlank(req.fullName()) || isBlank(req.email()))
            throw new BadRequestException("fullName and email are required.");
        userRepository.update(id, req);
        return findById(id);
    }

    public void delete(Long id) {
        if (userRepository.deleteById(id) == 0) throw new NotFoundException("User not found: " + id);
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.userId(), u.fullName(), u.email(), u.phone(), u.isActive(), u.createdAt());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
