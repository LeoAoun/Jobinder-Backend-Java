package br.com.jobinder.chatservice.dto.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationDTO(
        UUID id,
        UUID matchId,
        UUID clientUserId,
        UUID professionalUserId,
        LocalDateTime createdAt
) {}