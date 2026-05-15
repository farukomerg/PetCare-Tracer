package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UpdateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success("Users fetched successfully.", userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        return ApiResponse.success("User fetched successfully.", userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully.", createdUser));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        return ApiResponse.success("User updated successfully.", userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success("User deleted successfully.", null);
    }
}
