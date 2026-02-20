package com.careconnect.modules.auth;

import com.careconnect.modules.auth.dto.RegisterRequest;
import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.auth.entity.UserRole;
import com.careconnect.modules.auth.repository.UserRepository;
import com.careconnect.shared.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @InjectMocks AuthService authService;

    @Test
    void register_throws_when_email_already_exists() {
        var request = new RegisterRequest("test@test.com", "pass", "Ali", "Ben", UserRole.PATIENT, null);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void register_returns_tokens_on_success() {
        var request = new RegisterRequest("new@test.com", "pass", "Ali", "Ben", UserRole.PATIENT, null);
        var savedUser = User.builder()
                .email("new@test.com")
                .firstName("Ali")
                .lastName("Ben")
                .role(UserRole.PATIENT)
                .password("hashed")
                .build();

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        var response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }
}
