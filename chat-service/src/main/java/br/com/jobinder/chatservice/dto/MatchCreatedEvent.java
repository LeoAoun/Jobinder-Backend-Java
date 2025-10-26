package br.com.jobinder.chatservice.dto;

import java.io.Serializable;
import java.util.UUID;

public record MatchCreatedEvent(
        UUID matchId,
        UUID clientUserId,
        UUID professionalUserId
) implements Serializable {}