package com.careconnect.modules.booking.repository;

import com.careconnect.modules.booking.entity.Booking;
import com.careconnect.modules.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByPatientId(UUID patientId);
    List<Booking> findByProfessionalId(UUID professionalId);
    List<Booking> findByPatientIdAndStatus(UUID patientId, BookingStatus status);
}
