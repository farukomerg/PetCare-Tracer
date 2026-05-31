package com.petcare.user.service;

import com.petcare.user.dto.*;
import com.petcare.user.exception.BadRequestException;
import com.petcare.user.model.User;
import com.petcare.user.repository.UserRepository;
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

    public UserResponse register(CreateUserRequest req) {
        return userService.create(req);
    }

    public LoginResponse login(LoginRequest req) {
        if (req == null || isBlank(req.email()) || isBlank(req.password()))
            throw new BadRequestException("email and password are required.");
        User user = userRepository.findByEmail(req.email().trim())
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));
        if (!user.isActive()) throw new BadRequestException("User account is inactive.");
        if (!passwordEncoder.matches(req.password().trim(), user.passwordHash()))
            throw new BadRequestException("Invalid email or password.");
        return new LoginResponse(user.userId(), user.fullName(), user.email(), user.phone(), user.isActive(), user.createdAt());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
