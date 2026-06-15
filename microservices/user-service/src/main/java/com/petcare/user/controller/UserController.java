package com.petcare.user.controller;

import com.petcare.user.dto.*;
import com.petcare.user.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.success("Users fetched.", userService.findAll());
    }

    @GetMapping("/vets")
    public ApiResponse<List<UserResponse>> getVets() {
        return ApiResponse.success("Vets fetched.", userService.findByRole("VET"));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return ApiResponse.success("User fetched.", userService.findById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        return ApiResponse.success("User updated.", userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success("User deleted.", null);
    }
}
