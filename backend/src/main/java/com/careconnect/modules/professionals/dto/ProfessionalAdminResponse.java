package com.careconnect.modules.professionals.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfessionalAdminResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String specialization,
    String licenseNumber,
    String bio,
    String city,
    BigDecimal hourlyRate,
    boolean verified,
    Instant createdAt
) {}
