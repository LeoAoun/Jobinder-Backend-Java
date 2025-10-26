package br.com.jobinder.chatservice.dto.conversation;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ConversationCreateDTO(
        @NotBlank UUID matchId,
        @NotBlank UUID clientUserId,
        @NotBlank UUID professionalUserId
) {}