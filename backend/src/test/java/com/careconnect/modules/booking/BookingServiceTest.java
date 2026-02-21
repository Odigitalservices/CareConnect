package com.careconnect.modules.booking;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.auth.entity.UserRole;
import com.careconnect.modules.auth.repository.UserRepository;
import com.careconnect.modules.booking.dto.CreateBookingRequest;
import com.careconnect.modules.booking.entity.Booking;
import com.careconnect.modules.booking.entity.BookingStatus;
import com.careconnect.modules.booking.entity.VisitType;
import com.careconnect.modules.booking.repository.BookingRepository;
import com.careconnect.modules.professionals.entity.AvailabilitySlot;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.modules.professionals.repository.AvailabilitySlotRepository;
import com.careconnect.shared.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock UserRepository userRepository;
    @Mock AvailabilitySlotRepository slotRepository;

    @InjectMocks BookingService service;

    private User stubPatient() {
        return User.builder()
            .email("patient@test.com")
            .firstName("Sara")
            .lastName("Alami")
            .role(UserRole.PATIENT)
            .password("hashed")
            .build();
    }

    private Professional stubProfessional(boolean verified) {
        var profUser = User.builder()
            .email("doc@test.com")
            .firstName("Amira")
            .lastName("Khalil")
            .role(UserRole.PROFESSIONAL)
            .password("hashed")
            .build();
        return Professional.builder()
            .user(profUser)
            .specialization("Physiotherapy")
            .licenseNumber("LIC-001")
            .city("Casablanca")
            .hourlyRate(new BigDecimal("2500.00"))
            .verified(verified)
            .build();
    }

    private AvailabilitySlot stubSlot(Professional professional, boolean booked) {
        return AvailabilitySlot.builder()
            .professional(professional)
            .startTime(Instant.parse("2026-03-01T09:00:00Z"))
            .endTime(Instant.parse("2026-03-01T10:00:00Z"))
            .booked(booked)
            .build();
    }

    @Test
    void create_books_slot_and_returns_response() {
        var patient = stubPatient();
        var professional = stubProfessional(true);
        var slot = stubSlot(professional, false);
        var slotId = UUID.randomUUID();
        var request = new CreateBookingRequest(slotId, VisitType.CLINIC, null, null);

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(slotRepository.save(any(AvailabilitySlot.class))).thenAnswer(i -> i.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.create("patient@test.com", request);

        assertThat(result.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(result.visitType()).isEqualTo(VisitType.CLINIC);
        assertThat(result.professionalFirstName()).isEqualTo("Amira");
        assertThat(result.specialization()).isEqualTo("Physiotherapy");
        assertThat(slot.isBooked()).isTrue();
        verify(slotRepository).save(slot);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_throws_not_found_when_slot_missing() {
        var patient = stubPatient();
        var slotId = UUID.randomUUID();
        var request = new CreateBookingRequest(slotId, VisitType.CLINIC, null, null);

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("patient@test.com", request))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void create_throws_bad_request_when_slot_already_booked() {
        var patient = stubPatient();
        var professional = stubProfessional(true);
        var slot = stubSlot(professional, true);
        var slotId = UUID.randomUUID();
        var request = new CreateBookingRequest(slotId, VisitType.CLINIC, null, null);

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        assertThatThrownBy(() -> service.create("patient@test.com", request))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void create_throws_bad_request_when_professional_not_verified() {
        var patient = stubPatient();
        var professional = stubProfessional(false);
        var slot = stubSlot(professional, false);
        var slotId = UUID.randomUUID();
        var request = new CreateBookingRequest(slotId, VisitType.CLINIC, null, null);

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        assertThatThrownBy(() -> service.create("patient@test.com", request))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void getMyBookings_returns_list() {
        var patient = stubPatient();
        var patientId = UUID.randomUUID();
        ReflectionTestUtils.setField(patient, "id", patientId);

        var professional = stubProfessional(true);
        var slot = stubSlot(professional, true);
        var booking = Booking.builder()
            .patient(patient)
            .professional(professional)
            .slot(slot)
            .status(BookingStatus.PENDING)
            .visitType(VisitType.CLINIC)
            .build();

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(bookingRepository.findByPatientId(patientId)).thenReturn(List.of(booking));

        var result = service.getMyBookings("patient@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(BookingStatus.PENDING);
        assertThat(result.get(0).visitType()).isEqualTo(VisitType.CLINIC);
    }

    @Test
    void cancel_sets_status_to_cancelled_and_releases_slot() {
        var patient = stubPatient();
        var patientId = UUID.randomUUID();
        ReflectionTestUtils.setField(patient, "id", patientId);

        var professional = stubProfessional(true);
        var slot = stubSlot(professional, true);
        var bookingId = UUID.randomUUID();
        var booking = Booking.builder()
            .patient(patient)
            .professional(professional)
            .slot(slot)
            .status(BookingStatus.PENDING)
            .visitType(VisitType.CLINIC)
            .build();

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(slotRepository.save(any(AvailabilitySlot.class))).thenAnswer(i -> i.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        var result = service.cancel(bookingId, "patient@test.com");

        assertThat(result.status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(slot.isBooked()).isFalse();
        verify(slotRepository).save(slot);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_throws_forbidden_when_not_owner() {
        var bookingPatient = stubPatient();
        var bookingPatientId = UUID.randomUUID();
        ReflectionTestUtils.setField(bookingPatient, "id", bookingPatientId);

        var otherPatient = User.builder()
            .email("other@test.com")
            .firstName("Other")
            .lastName("User")
            .role(UserRole.PATIENT)
            .password("hashed")
            .build();
        var otherPatientId = UUID.randomUUID();
        ReflectionTestUtils.setField(otherPatient, "id", otherPatientId);

        var professional = stubProfessional(true);
        var slot = stubSlot(professional, true);
        var bookingId = UUID.randomUUID();
        var booking = Booking.builder()
            .patient(bookingPatient)
            .professional(professional)
            .slot(slot)
            .status(BookingStatus.PENDING)
            .visitType(VisitType.CLINIC)
            .build();

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherPatient));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.cancel(bookingId, "other@test.com"))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void cancel_throws_bad_request_when_already_cancelled() {
        var patient = stubPatient();
        var patientId = UUID.randomUUID();
        ReflectionTestUtils.setField(patient, "id", patientId);

        var professional = stubProfessional(true);
        var slot = stubSlot(professional, false);
        var bookingId = UUID.randomUUID();
        var booking = Booking.builder()
            .patient(patient)
            .professional(professional)
            .slot(slot)
            .status(BookingStatus.CANCELLED)
            .visitType(VisitType.CLINIC)
            .build();

        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(patient));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.cancel(bookingId, "patient@test.com"))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
}
