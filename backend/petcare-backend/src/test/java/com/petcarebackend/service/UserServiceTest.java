package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UserResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.exception.NotFoundException;
import com.petcarebackend.model.User;
import com.petcarebackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * [RED → GREEN] UserService için TDD testleri.
 * IUserService arayüzü üzerinden davranış doğrulanır.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser() {
        return new User(1L, "Ali Veli", "ali@example.com", "$2a$hash", "05551234567",
                LocalDateTime.of(2026, 1, 1, 10, 0), true);
    }

    // --- findAll ---

    @Test
    void findAllReturnsMappedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser()));

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ali Veli", result.get(0).fullName());
    }

    // --- findById ---

    @Test
    void findByIdReturnsUserWhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));

        UserResponse result = userService.findById(1L);

        assertEquals("ali@example.com", result.email());
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(99L));
    }

    // --- create ---

    @Test
    void createThrowsBadRequestWhenFullNameIsBlank() {
        CreateUserRequest request = new CreateUserRequest("  ", "test@mail.com", "pass123", null);

        assertThrows(BadRequestException.class, () -> userService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenEmailIsBlank() {
        CreateUserRequest request = new CreateUserRequest("Ali Veli", "", "pass123", null);

        assertThrows(BadRequestException.class, () -> userService.create(request));
    }

    @Test
    void createThrowsBadRequestWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("Ali Veli", "ali@example.com", "pass123", null);
        when(userRepository.findByEmail("ali@example.com")).thenReturn(Optional.of(sampleUser()));

        assertThrows(BadRequestException.class, () -> userService.create(request));
    }

    @Test
    void createHashesPasswordAndSavesUser() {
        CreateUserRequest request = new CreateUserRequest("Yeni Kullanici", "yeni@example.com", "sifre123", null);
        when(userRepository.findByEmail("yeni@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$hashed$");
        when(userRepository.save(any(), anyString())).thenReturn(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                new User(2L, "Yeni Kullanici", "yeni@example.com", "$hashed$", null, LocalDateTime.now(), true)));

        UserResponse result = userService.create(request);

        assertEquals(2L, result.userId());
        assertEquals("Yeni Kullanici", result.fullName());
        verify(passwordEncoder).encode("sifre123");
    }

    // --- delete ---

    @Test
    void deleteThrowsNotFoundWhenUserMissing() {
        when(userRepository.deleteById(99L)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.delete(99L));
    }
}
