package br.com.jobinder.chatservice.service;

import br.com.jobinder.chatservice.domain.conversation.Conversation;
import br.com.jobinder.chatservice.domain.conversation.ConversationRepository;
import br.com.jobinder.chatservice.dto.MatchCreatedEvent;
import br.com.jobinder.chatservice.dto.conversation.ConversationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    @DisplayName("Should create conversation successfully when it does not exist")
    void createConversation_WhenNewMatch_ShouldSaveAndReturnEntity() {
        // Given
        UUID matchId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID professionalId = UUID.randomUUID();
        MatchCreatedEvent event = new MatchCreatedEvent(matchId, clientId, professionalId);

        // Mocking
        when(conversationRepository.existsByMatchId(matchId))
                .thenReturn(false);

        // Mocking
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conv = invocation.getArgument(0);
            conv.setId(UUID.randomUUID());
            conv.setCreatedAt(LocalDateTime.now());
            return conv;
        });

        // When
        Conversation result = conversationService.createConversation(event);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(matchId, result.getMatchId());
        assertEquals(clientId, result.getClientUserId());
        assertEquals(professionalId, result.getProfessionalUserId());

        // Verify
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should return existing conversation when match ID already exists (Idempotency)")
    void createConversation_WhenConversationExists_ShouldReturnExisting() {
        // Given
        UUID matchId = UUID.randomUUID();
        MatchCreatedEvent event = new MatchCreatedEvent(matchId, UUID.randomUUID(), UUID.randomUUID());

        Conversation existingConversation = new Conversation();
        existingConversation.setId(UUID.randomUUID());
        existingConversation.setMatchId(matchId);

        // Mocking
        when(conversationRepository.existsByMatchId(matchId))
                .thenReturn(true);
        // Mocking
        when(conversationRepository.findByMatchId(matchId))
                .thenReturn(Optional.of(existingConversation));

        // When
        Conversation result = conversationService.createConversation(event);

        // Then
        assertNotNull(result);
        assertEquals(existingConversation.getId(), result.getId());

        // Verify
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should return ConversationDTO when found by ID")
    void findConversationDTOById_WhenFound_ShouldReturnDTO() {
        // Given
        UUID conversationId = UUID.randomUUID();
        Conversation conversation = new Conversation(
                conversationId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                null
        );

        // Mocking
        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.of(conversation));

        // When
        ConversationDTO result = conversationService.findConversationDTOById(conversationId);

        // Then
        assertNotNull(result);
        assertEquals(conversationId, result.id());
        assertEquals(conversation.getMatchId(), result.matchId());
        assertEquals(conversation.getClientUserId(), result.clientUserId());
    }

    @Test
    @DisplayName("Should throw RuntimeException when conversation not found by ID")
    void findConversationDTOById_WhenNotFound_ShouldThrowException() {
        // Given
        UUID conversationId = UUID.randomUUID();

        // Mocking
        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            conversationService.findConversationDTOById(conversationId);
        });

        assertEquals("Conversation not found with ID: " + conversationId, exception.getMessage());
    }

    @Test
    @DisplayName("Should return list of ConversationDTOs for a specific user")
    void findConversationDTOsByUserId_ShouldReturnList() {
        // Given
        UUID userId = UUID.randomUUID();
        Conversation conv1 = new Conversation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                LocalDateTime.now(),
                null
        );
        Conversation conv2 = new Conversation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                userId,
                LocalDateTime.now(),
                null
        );

        // Mocking
        when(conversationRepository.findAllByClientUserIdOrProfessionalUserId(userId, userId))
                .thenReturn(List.of(conv1, conv2));

        // When
        List<ConversationDTO> result = conversationService.findConversationDTOsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(conv1.getId(), result.get(0).id());
        assertEquals(conv2.getId(), result.get(1).id());
    }

    @Test
    @DisplayName("Should return all conversations as DTOs")
    void findAllConversationDTOs_ShouldReturnList() {
        // Given
        Conversation conv1 = new Conversation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                null
        );

        // Mocking
        when(conversationRepository.findAll())
                .thenReturn(List.of(conv1));

        // When
        List<ConversationDTO> result = conversationService.findAllConversationDTOs();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(conv1.getId(), result.get(0).id());
    }
}