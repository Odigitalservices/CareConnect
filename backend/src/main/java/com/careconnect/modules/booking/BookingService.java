package com.careconnect.modules.booking;

import com.careconnect.modules.auth.repository.UserRepository;
import com.careconnect.modules.booking.dto.BookingResponse;
import com.careconnect.modules.booking.dto.CreateBookingRequest;
import com.careconnect.modules.booking.entity.Booking;
import com.careconnect.modules.booking.entity.BookingStatus;
import com.careconnect.modules.booking.repository.BookingRepository;
import com.careconnect.modules.professionals.repository.AvailabilitySlotRepository;
import com.careconnect.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AvailabilitySlotRepository slotRepository;

    @Transactional
    public BookingResponse create(String patientEmail, CreateBookingRequest request) {
        var patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> AppException.notFound("User not found"));

        var slot = slotRepository.findByIdForUpdate(request.slotId())
            .orElseThrow(() -> AppException.notFound("Slot not found"));

        if (slot.isBooked()) {
            throw AppException.badRequest("Slot is already booked");
        }
        if (!slot.getProfessional().isVerified()) {
            throw AppException.badRequest("Professional is not verified");
        }

        slot.setBooked(true);
        slotRepository.save(slot);

        var booking = Booking.builder()
            .patient(patient)
            .professional(slot.getProfessional())
            .slot(slot)
            .status(BookingStatus.PENDING)
            .visitType(request.visitType())
            .address(request.address())
            .notes(request.notes())
            .build();
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String patientEmail) {
        var patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> AppException.notFound("User not found"));
        return bookingRepository.findByPatientId(patient.getId())
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookingResponse cancel(UUID bookingId, String patientEmail) {
        var patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> AppException.notFound("User not found"));

        var booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> AppException.notFound("Booking not found"));

        if (!booking.getPatient().getId().equals(patient.getId())) {
            throw AppException.forbidden("Not your booking");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED ||
            booking.getStatus() == BookingStatus.CANCELLED) {
            throw AppException.badRequest("Booking cannot be cancelled in its current status");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSlot().setBooked(false);
        slotRepository.save(booking.getSlot());
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking b) {
        var professional = b.getProfessional();
        var slot = b.getSlot();
        return new BookingResponse(
            b.getId(),
            professional.getId(),
            professional.getUser().getFirstName(),
            professional.getUser().getLastName(),
            professional.getSpecialization(),
            slot.getId(),
            slot.getStartTime(),
            slot.getEndTime(),
            b.getStatus(),
            b.getVisitType(),
            b.getAddress(),
            b.getNotes(),
            b.getCreatedAt()
        );
    }
}
