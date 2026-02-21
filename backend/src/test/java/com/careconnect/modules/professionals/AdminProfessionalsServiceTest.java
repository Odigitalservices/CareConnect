package com.careconnect.modules.professionals;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.auth.entity.UserRole;
import com.careconnect.modules.auth.repository.UserRepository;
import com.careconnect.modules.professionals.dto.*;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.modules.professionals.repository.ProfessionalRepository;
import com.careconnect.shared.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProfessionalsServiceTest {

    @Mock ProfessionalRepository professionalRepository;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AdminProfessionalsService service;

    private Professional stubProfessional(boolean verified) {
        var user = User.builder()
            .email("doc@test.com").firstName("Amira").lastName("Khalil")
            .phone("+21255500011").role(UserRole.PROFESSIONAL).password("hashed")
            .build();
        return Professional.builder()
            .user(user).specialization("Physiotherapy")
            .licenseNumber("LIC-001").city("Casablanca")
            .hourlyRate(new BigDecimal("2500.00")).verified(verified).build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listAll_returns_all_professionals_including_unverified() {
        var unverified = stubProfessional(false);
        var verified = stubProfessional(true);
        when(professionalRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(verified, unverified)));

        var result = service.listAll(0, 20);

        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void create_saves_user_and_professional_and_returns_response() {
        when(userRepository.existsByEmail("doc@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        var request = new CreateProfessionalRequest(
            "doc@test.com", "password123", "Amira", "Khalil", null,
            "Physiotherapy", "LIC-001", "Casablanca", new BigDecimal("2500.00"), null);

        var result = service.create(request);

        assertThat(result.email()).isEqualTo("doc@test.com");
        assertThat(result.firstName()).isEqualTo("Amira");
        assertThat(result.verified()).isFalse();
        verify(userRepository).save(any(User.class));
        verify(professionalRepository).save(any(Professional.class));
    }

    @Test
    void create_throws_bad_request_when_email_taken() {
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        var request = new CreateProfessionalRequest(
            "taken@test.com", "password123", "A", "B", null,
            "Spec", "LIC-002", null, null, null);

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void update_patches_only_provided_fields() {
        var professional = stubProfessional(false);
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.of(professional));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        var request = new UpdateProfessionalRequest("Cardiology", null, null, null);
        var result = service.update(UUID.randomUUID(), request);

        assertThat(result.specialization()).isEqualTo("Cardiology");
        assertThat(result.city()).isEqualTo("Casablanca"); // unchanged
    }

    @Test
    void update_throws_not_found_for_missing_professional() {
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(UUID.randomUUID(), new UpdateProfessionalRequest(null, null, null, null)))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_removes_professional() {
        var professional = stubProfessional(false);
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.of(professional));

        service.delete(UUID.randomUUID());

        verify(professionalRepository).delete(professional);
    }

    @Test
    void delete_throws_not_found_for_missing_professional() {
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(UUID.randomUUID()))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void toggleVerify_flips_verified_flag_to_true() {
        var professional = stubProfessional(false);
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.of(professional));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.toggleVerify(UUID.randomUUID());

        assertThat(result.verified()).isTrue();
    }

    @Test
    void toggleVerify_flips_verified_flag_to_false() {
        var professional = stubProfessional(true);
        when(professionalRepository.findById(any(UUID.class))).thenReturn(Optional.of(professional));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.toggleVerify(UUID.randomUUID());

        assertThat(result.verified()).isFalse();
    }
}
