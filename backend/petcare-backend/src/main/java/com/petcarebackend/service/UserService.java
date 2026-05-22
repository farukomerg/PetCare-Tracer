package com.petcarebackend.service;

import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UpdateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.User;
import com.petcarebackend.repository.UserRepository;
import com.petcarebackend.util.ValidationUtils;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /** @deprecated Geriye dönük uyumluluk için; findAll() kullanınız. */
    @Deprecated
    public List<UserResponse> getAllUsers() {
        return findAll();
    }

    @Override
    public UserResponse findById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    /** @deprecated Geriye dönük uyumluluk için; findById() kullanınız. */
    @Deprecated
    public UserResponse getUserById(Long userId) {
        return findById(userId);
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        validateCreateUserRequest(request);

        userRepository.findByEmail(request.email())
                .ifPresent(existing -> {
                    throw new BadRequestException("Email is already registered.");
                });

        String passwordHash = passwordEncoder.encode(request.password().trim());
        Long userId = userRepository.save(request, passwordHash);
        return findById(userId);
    }

    /** @deprecated Geriye dönük uyumluluk için; create() kullanınız. */
    @Deprecated
    public UserResponse createUser(CreateUserRequest request) {
        return create(request);
    }

    @Override
    public UserResponse update(Long userId, UpdateUserRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        validateUpdateUserRequest(request);

        userRepository.findByEmail(request.email())
                .ifPresent(foundUser -> {
                    if (!foundUser.userId().equals(userId)) {
                        throw new BadRequestException("Email is already registered.");
                    }
                });

        userRepository.update(userId, request);
        return findById(userId);
    }

    /** @deprecated Geriye dönük uyumluluk için; update() kullanınız. */
    @Deprecated
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        return update(userId, request);
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.deleteById(userId) == 0) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    /** @deprecated Geriye dönük uyumluluk için; delete() kullanınız. */
    @Deprecated
    public void deleteUser(Long userId) {
        delete(userId);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (ValidationUtils.isBlank(request.fullName())) {
            throw new BadRequestException("fullName is required.");
        }
        if (ValidationUtils.isBlank(request.email())) {
            throw new BadRequestException("email is required.");
        }
        if (ValidationUtils.isBlank(request.password())) {
            throw new BadRequestException("password is required.");
        }
    }

    private void validateUpdateUserRequest(UpdateUserRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (ValidationUtils.isBlank(request.fullName())) {
            throw new BadRequestException("fullName is required.");
        }
        if (ValidationUtils.isBlank(request.email())) {
            throw new BadRequestException("email is required.");
        }
        if (request.isActive() == null) {
            throw new BadRequestException("isActive is required.");
        }
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
