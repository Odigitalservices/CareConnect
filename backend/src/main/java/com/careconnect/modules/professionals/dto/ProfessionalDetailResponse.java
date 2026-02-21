package com.careconnect.modules.professionals.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProfessionalDetailResponse(
    UUID id,
    String firstName,
    String lastName,
    String specialization,
    String bio,
    String city,
    BigDecimal hourlyRate,
    List<AvailabilitySlotResponse> availableSlots,
    Instant createdAt
) {}
