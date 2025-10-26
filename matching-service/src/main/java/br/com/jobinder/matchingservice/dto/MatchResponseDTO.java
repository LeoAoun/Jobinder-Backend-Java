package br.com.jobinder.matchingservice.dto;


import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MatchResponseDTO(
        @NotBlank UUID matchId,
        @NotBlank String message
) {}