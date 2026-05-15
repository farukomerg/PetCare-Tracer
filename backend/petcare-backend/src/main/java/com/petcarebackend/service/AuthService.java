package com.petcarebackend.service;

import com.petcarebackend.dto.auth.LoginRequest;
import com.petcarebackend.dto.auth.LoginResponse;
import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.model.User;
import com.petcarebackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(CreateUserRequest request) {
        return userService.createUser(request);
    }

    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);

        User user = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        if (!user.isActive()) {
            throw new BadRequestException("User account is inactive.");
        }

        if (!passwordEncoder.matches(request.password().trim(), user.passwordHash())) {
            throw new BadRequestException("Invalid email or password.");
        }

        return new LoginResponse(
                user.userId(),
                user.fullName(),
                user.email(),
                user.phone(),
                user.isActive(),
                user.createdAt()
        );
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new BadRequestException("email is required.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("password is required.");
        }
    }
}
