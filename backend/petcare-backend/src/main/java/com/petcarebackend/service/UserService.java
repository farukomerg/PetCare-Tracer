package com.petcarebackend.service;

import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UpdateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.User;
import com.petcarebackend.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    public UserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        userRepository.findByEmail(request.email())
                .ifPresent(existing -> {
                    throw new BadRequestException("Email is already registered.");
                });

        String passwordHash = passwordEncoder.encode(request.password().trim());
        Long userId = userRepository.save(request, passwordHash);
        return getUserById(userId);
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        validateUpdateUserRequest(request);

        userRepository.findByEmail(request.email())
                .ifPresent(foundUser -> {
                    if (!foundUser.userId().equals(userId)) {
                        throw new BadRequestException("Email is already registered.");
                    }
                });

        userRepository.update(userId, request);
        return getUserById(userId);
    }

    public void deleteUser(Long userId) {
        if (userRepository.deleteById(userId) == 0) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (isBlank(request.fullName())) {
            throw new BadRequestException("fullName is required.");
        }
        if (isBlank(request.email())) {
            throw new BadRequestException("email is required.");
        }
        if (isBlank(request.password())) {
            throw new BadRequestException("password is required.");
        }
    }

    private void validateUpdateUserRequest(UpdateUserRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (isBlank(request.fullName())) {
            throw new BadRequestException("fullName is required.");
        }
        if (isBlank(request.email())) {
            throw new BadRequestException("email is required.");
        }
        if (request.isActive() == null) {
            throw new BadRequestException("isActive is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.userId(),
                user.fullName(),
                user.email(),
                user.phone(),
                user.isActive(),
                user.createdAt()
        );
    }
}
