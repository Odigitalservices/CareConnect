package com.careconnect.modules.professionals.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProfessionalRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phone,
    @NotBlank String specialization,
    @NotBlank String licenseNumber,
    String city,
    BigDecimal hourlyRate,
    String bio
) {}
