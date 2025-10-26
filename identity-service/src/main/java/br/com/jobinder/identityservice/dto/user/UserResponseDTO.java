package br.com.jobinder.identityservice.dto.user;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserResponseDTO(
        @NotBlank UUID id,
        @NotBlank String phone,
        @NotBlank String firstName,
        @NotBlank String lastName
) {}
