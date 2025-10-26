package br.com.jobinder.chatservice.dto.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDTO(
        UUID id,
        UUID senderId,
        String content,
        LocalDateTime sentAt,
        LocalDateTime readAt,
        UUID conversationId
) {}