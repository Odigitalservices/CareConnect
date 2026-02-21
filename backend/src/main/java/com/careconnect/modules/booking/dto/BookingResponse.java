package com.careconnect.modules.booking.dto;

import com.careconnect.modules.booking.entity.BookingStatus;
import com.careconnect.modules.booking.entity.VisitType;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
    UUID id,
    UUID professionalId,
    String professionalFirstName,
    String professionalLastName,
    String specialization,
    UUID slotId,
    Instant slotStart,
    Instant slotEnd,
    BookingStatus status,
    VisitType visitType,
    String address,
    String notes,
    Instant createdAt
) {}
