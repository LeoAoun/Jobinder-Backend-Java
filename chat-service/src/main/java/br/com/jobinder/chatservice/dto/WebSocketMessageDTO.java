package br.com.jobinder.chatservice.dto;

import java.util.UUID;

public record WebSocketMessageDTO(
        UUID conversationId,
        UUID senderId,
        String content
) {}