package br.com.jobinder.identityservice.dto.location;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record LocationDTO(
        @NotBlank UUID id,
        @NotBlank String city,
        @NotBlank String state
) {}

