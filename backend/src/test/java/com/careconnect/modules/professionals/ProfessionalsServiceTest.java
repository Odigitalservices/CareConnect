package com.careconnect.modules.professionals;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.auth.entity.UserRole;
import com.careconnect.modules.professionals.entity.AvailabilitySlot;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.modules.professionals.repository.AvailabilitySlotRepository;
import com.careconnect.modules.professionals.repository.ProfessionalRepository;
import com.careconnect.shared.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionalsServiceTest {

    @Mock ProfessionalRepository professionalRepository;
    @Mock AvailabilitySlotRepository availabilitySlotRepository;

    @InjectMocks ProfessionalsService professionalsService;

    @Captor ArgumentCaptor<Pageable> pageableCaptor;

    // -----------------------------------------------------------------------
    // Helper stubs
    // -----------------------------------------------------------------------

    private Professional stubVerifiedProfessional() {
        var user = User.builder()
                .email("doc@test.com").firstName("Amira").lastName("Khalil")
                .phone("+21255500011").role(UserRole.PROFESSIONAL).password("hashed")
                .build();
        return Professional.builder()
                .user(user).specialization("Physiotherapy")
                .city("Casablanca").hourlyRate(new BigDecimal("2500.00"))
                .verified(true).licenseNumber("LIC-001")
                .build();
    }

    private AvailabilitySlot stubSlot() {
        return AvailabilitySlot.builder()
                .professional(null) // not needed for mapping
                .startTime(Instant.parse("2026-03-01T09:00:00Z"))
                .endTime(Instant.parse("2026-03-01T10:00:00Z"))
                .booked(false)
                .build();
    }

    // -----------------------------------------------------------------------
    // listProfessionals tests
    // -----------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void list_returns_page_of_verified_professionals() {
        when(professionalRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(stubVerifiedProfessional())));

        var result = professionalsService.listProfessionals(null, null, null, null, 0, 20);

        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.content().get(0).firstName()).isEqualTo("Amira");
        assertThat(result.page()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_caps_size_at_50() {
        when(professionalRepository.findAll(any(Specification.class), pageableCaptor.capture()))
                .thenReturn(Page.empty());

        professionalsService.listProfessionals(null, null, null, null, 0, 200);

        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void list_throws_bad_request_when_minRate_exceeds_maxRate() {
        assertThatThrownBy(() ->
                professionalsService.listProfessionals(
                        null, null,
                        new BigDecimal("5000"), new BigDecimal("1000"),
                        0, 20))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("minRate");
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_returns_empty_page_when_no_match() {
        when(professionalRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        var result = professionalsService.listProfessionals("Marrakesh", null, null, null, 0, 20);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
        assertThat(result.last()).isTrue();
    }

    @Test
    void list_rejects_negative_page() {
        assertThatThrownBy(() ->
                professionalsService.listProfessionals(null, null, null, null, -1, 20))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_clamps_size_below_1_to_1() {
        when(professionalRepository.findAll(any(Specification.class), pageableCaptor.capture()))
                .thenReturn(Page.empty());

        professionalsService.listProfessionals(null, null, null, null, 0, 0);

        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(1);
    }

    // -----------------------------------------------------------------------
    // getProfessionalById tests
    // -----------------------------------------------------------------------

    @Test
    void getById_returns_detail_with_slots() {
        when(professionalRepository.findById(any()))
                .thenReturn(Optional.of(stubVerifiedProfessional()));
        when(availabilitySlotRepository.findByProfessionalIdAndBookedFalseOrderByStartTimeAsc(any()))
                .thenReturn(List.of(stubSlot(), stubSlot()));

        var result = professionalsService.getProfessionalById(UUID.randomUUID());

        assertThat(result.availableSlots()).hasSize(2);
        assertThat(result.specialization()).isEqualTo("Physiotherapy");
    }

    @Test
    void getById_throws_not_found_when_id_does_not_exist() {
        when(professionalRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionalsService.getProfessionalById(UUID.randomUUID()))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getById_throws_not_found_when_not_verified() {
        var user = User.builder()
                .email("unverified@test.com").firstName("Unverified").lastName("User")
                .phone("+21200000000").role(UserRole.PROFESSIONAL).password("hashed")
                .build();
        var unverifiedProfessional = Professional.builder()
                .user(user).specialization("Cardiology")
                .city("Rabat").hourlyRate(new BigDecimal("1500.00"))
                .verified(false).licenseNumber("LIC-002")
                .build();

        when(professionalRepository.findById(any())).thenReturn(Optional.of(unverifiedProfessional));

        assertThatThrownBy(() -> professionalsService.getProfessionalById(UUID.randomUUID()))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getById_returns_empty_slots_when_none_available() {
        when(professionalRepository.findById(any()))
                .thenReturn(Optional.of(stubVerifiedProfessional()));
        when(availabilitySlotRepository.findByProfessionalIdAndBookedFalseOrderByStartTimeAsc(any()))
                .thenReturn(List.of());

        var result = professionalsService.getProfessionalById(UUID.randomUUID());

        assertThat(result.availableSlots()).isNotNull();
        assertThat(result.availableSlots()).isEmpty();
    }
}
