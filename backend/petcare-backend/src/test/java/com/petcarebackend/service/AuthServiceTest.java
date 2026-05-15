package com.petcarebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.petcarebackend.dto.auth.LoginRequest;
import com.petcarebackend.dto.auth.LoginResponse;
import com.petcarebackend.exception.BadRequestException;
import com.petcarebackend.model.User;
import com.petcarebackend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsUserWhenCredentialsAreValid() {
        User user = new User(7L, "Demo User", "demo@example.com", "$2a$10$hash", "05550000000",
                LocalDateTime.of(2026, 5, 15, 10, 0), true);

        when(userRepository.findByEmail("demo@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "$2a$10$hash")).thenReturn(true);

        LoginResponse response = authService.login(new LoginRequest("demo@example.com", "123456"));

        assertEquals(7L, response.userId());
        assertEquals("Demo User", response.fullName());
        assertEquals("demo@example.com", response.email());
    }

    @Test
    void loginThrowsWhenUserInactive() {
        User user = new User(8L, "Passive User", "passive@example.com", "$2a$10$hash", "05550000001",
                LocalDateTime.now(), false);

        when(userRepository.findByEmail("passive@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> authService.login(new LoginRequest("passive@example.com", "123456")));
    }
}
