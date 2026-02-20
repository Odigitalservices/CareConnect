package com.careconnect.modules.auth.dto;

import com.careconnect.modules.auth.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull UserRole role,
    String phone
) {}
