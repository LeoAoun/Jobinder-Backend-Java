package br.com.jobinder.identityservice.dto.serviceprofile;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

// "Flat" DTO to facilitate front-end consumption
public record ServiceProfileResponseDTO(
        @NotBlank UUID serviceProfileId,
        @NotBlank UUID userId,
        @NotBlank String specialtyName,
        @NotBlank String locationCity,
        @NotBlank String locationState,
        @NotBlank String serviceImageUrl,
        @NotBlank Integer servicesPerformed,
        @NotBlank Float rating,
        @NotBlank String availability,
        @NotBlank String description,
        @NotBlank LocalDateTime createdAt,
        @NotBlank LocalDateTime updatedAt
) {}