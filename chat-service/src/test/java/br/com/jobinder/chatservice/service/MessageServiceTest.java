package br.com.jobinder.chatservice.service;

import br.com.jobinder.chatservice.domain.conversation.ConversationRepository;
import br.com.jobinder.chatservice.domain.conversation.Conversation;
import br.com.jobinder.chatservice.domain.message.Message;
import br.com.jobinder.chatservice.domain.message.MessageRepository;
import br.com.jobinder.chatservice.dto.WebSocketMessageDTO;
import br.com.jobinder.chatservice.dto.message.MessageDTO;
import br.com.jobinder.chatservice.infra.exception.ConversationNotFoundException;
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
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    @DisplayName("Should save message successfully when conversation exists")
    void saveMessage_WhenConversationExists_ShouldSaveAndReturnEntity() {
        // Given
        UUID conversationId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String content = "Hello Jobinder!";

        WebSocketMessageDTO messageDto = new WebSocketMessageDTO(conversationId, senderId, content);

        Conversation conversation = new Conversation();
        conversation.setId(conversationId);

        // Mocking
        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.of(conversation));

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            msg.setId(UUID.randomUUID());
            msg.setSentAt(LocalDateTime.now());
            return msg;
        });

        // When
        Message result = messageService.saveMessage(messageDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(content, result.getContent());
        assertEquals(senderId, result.getSenderId());
        assertEquals(conversation, result.getConversation());

        // Verify
        verify(conversationRepository).findById(conversationId);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw ConversationNotFoundException when saving message for non-existent conversation")
    void saveMessage_WhenConversationNotFound_ShouldThrowException() {
        // Given
        UUID conversationId = UUID.randomUUID();
        WebSocketMessageDTO messageDto = new WebSocketMessageDTO(conversationId, UUID.randomUUID(), "Content");

        // Mocking
        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.empty());

        // When & Then
        ConversationNotFoundException exception = assertThrows(ConversationNotFoundException.class, () -> {
            messageService.saveMessage(messageDto);
        });

        assertEquals("Conversation not found with ID: " + conversationId, exception.getMessage());

        // Verify
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should return list of MessageDTOs when conversation exists")
    void findMessagesDTOByConversationId_WhenConversationExists_ShouldReturnList() {
        // Given
        UUID conversationId = UUID.randomUUID();
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);

        Message msg1 = new Message(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Hi",
                LocalDateTime.now(),
                null,
                conversation
        );
        Message msg2 = new Message(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Hello",
                LocalDateTime.now(),
                null,
                conversation
        );

        // Mocking
        when(conversationRepository.existsById(conversationId))
                .thenReturn(true);

        when(messageRepository.findByConversationIdOrderBySentAtAsc(conversationId))
                .thenReturn(List.of(msg1, msg2));

        // When
        List<MessageDTO> result = messageService.findMessagesDTOByConversationId(conversationId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(msg1.getId(), result.get(0).id());
        assertEquals(msg2.getContent(), result.get(1).content());
        assertEquals(conversationId, result.get(0).conversationId());

        // Verify
        verify(conversationRepository).existsById(conversationId);
        verify(messageRepository).findByConversationIdOrderBySentAtAsc(conversationId);
    }

    @Test
    @DisplayName("Should throw ConversationNotFoundException when finding messages for non-existent conversation")
    void findMessagesDTOByConversationId_WhenConversationNotFound_ShouldThrowException() {
        // Given
        UUID conversationId = UUID.randomUUID();

        // Mocking
        when(conversationRepository.existsById(conversationId))
                .thenReturn(false);

        // When & Then
        ConversationNotFoundException exception = assertThrows(ConversationNotFoundException.class, () -> {
            messageService.findMessagesDTOByConversationId(conversationId);
        });

        assertTrue(exception.getMessage().contains("Cannot find messages"));

        // Verify
        verify(messageRepository, never()).findByConversationIdOrderBySentAtAsc(any());
    }

    @Test
    @DisplayName("Should delete messages successfully when conversation exists")
    void deleteMessagesByConversationId_WhenConversationExists_ShouldDelete() {
        // Given
        UUID conversationId = UUID.randomUUID();

        // Mocking
        when(conversationRepository.existsById(conversationId))
                .thenReturn(true);

        // When
        messageService.deleteMessagesByConversationId(conversationId);

        // Verify
        verify(conversationRepository).existsById(conversationId);
        verify(messageRepository).deleteByConversationId(conversationId);
    }

    @Test
    @DisplayName("Should throw ConversationNotFoundException when deleting messages for non-existent conversation")
    void deleteMessagesByConversationId_WhenConversationNotFound_ShouldThrowException() {
        // Given
        UUID conversationId = UUID.randomUUID();

        // Mocking
        when(conversationRepository.existsById(conversationId))
                .thenReturn(false);

        // When & Then
        ConversationNotFoundException exception = assertThrows(ConversationNotFoundException.class, () -> {
            messageService.deleteMessagesByConversationId(conversationId);
        });

        assertEquals("Conversation not found with ID: " + conversationId, exception.getMessage());

        // Verify
        verify(messageRepository, never()).deleteByConversationId(any());
    }
}