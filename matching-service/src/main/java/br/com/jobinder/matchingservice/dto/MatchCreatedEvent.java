package br.com.jobinder.matchingservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.UUID;

public record MatchCreatedEvent(
        @NotBlank UUID matchId,
        @NotBlank UUID clientUserId,
        @NotBlank UUID professionalUserId
) implements Serializable {}