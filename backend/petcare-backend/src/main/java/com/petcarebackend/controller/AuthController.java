package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.auth.LoginRequest;
import com.petcarebackend.dto.auth.LoginResponse;
import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody CreateUserRequest request) {
        UserResponse createdUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully.", createdUser));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful.", authService.login(request));
    }
}
