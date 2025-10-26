package br.com.jobinder.matchingservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MatchCreateDTO(
        @NotBlank(message = "Professional ID is required")
        UUID professionalUserId
) {}