package com.careconnect.modules.professionals.dto;

import java.math.BigDecimal;

public record UpdateProfessionalRequest(
    String specialization,
    String bio,
    String city,
    BigDecimal hourlyRate
) {}
