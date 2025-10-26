package br.com.jobinder.identityservice.dto.specialty;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SpecialtyDTO(
        @NotBlank UUID id,
        @NotBlank String name
) {}
