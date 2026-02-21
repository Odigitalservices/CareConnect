package com.careconnect.modules.professionals.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfessionalSummaryResponse(
    UUID id, String firstName, String lastName,
    String specialization, String city,
    BigDecimal hourlyRate, Instant createdAt
) {}
