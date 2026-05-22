package com.petcarebackend.service;

import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UpdateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import java.util.List;

/**
 * Kullanıcı yönetimi servis sözleşmesi.
 * SOLID — Dependency Inversion Principle (DIP).
 */
public interface IUserService extends CrudService<UserResponse, Long, CreateUserRequest> {

    UserResponse update(Long userId, UpdateUserRequest request);
}
