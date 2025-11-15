package br.com.jobinder.chatservice.handler;

import br.com.jobinder.chatservice.domain.conversation.Conversation;
import br.com.jobinder.chatservice.domain.message.Message;
import br.com.jobinder.chatservice.dto.WebSocketMessageDTO;
import br.com.jobinder.chatservice.service.ConversationService;
import br.com.jobinder.chatservice.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    // Map to store active WebSocket sessions
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    private final MessageService messageService;
    private final ConversationService conversationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // Get the userId from the session attributes (set by the interceptor)
            String userIdString = (String) session.getAttributes().get("userId");
            if (userIdString == null) {
                throw new IllegalArgumentException("userId is missing in session attributes");
            }

            // Store the session associated with the userId
            UUID userId = UUID.fromString(userIdString);
            sessions.put(userId, session);
            log.info("NEW CONNECTION: {} with session ID: {}", userId, session.getId());

        } catch (Exception e) {
            // If authentication fails, close the session
            log.warn("Handshacke failure. Clossing session {}: {}", session.getId(), e.getMessage());
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Autenticação inválida"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // Parse the incoming message
            WebSocketMessageDTO messageDto = objectMapper.readValue(message.getPayload(), WebSocketMessageDTO.class);

            // Save the message to the database
            Message savedMessage = messageService.saveMessage(messageDto);

            // Determine the recipient based on the conversation
            Conversation conversation = savedMessage.getConversation();
            UUID recipientId = conversation.getClientUserId().equals(savedMessage.getSenderId())
                    ? conversation.getProfessionalUserId()
                    : conversation.getClientUserId();

            // Send the message to the recipient if they are online
            WebSocketSession recipientSession = sessions.get(recipientId);
            if (recipientSession != null && recipientSession.isOpen()) {
                try {
                    recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(savedMessage)));
                    log.info("Message {} sent to {}", savedMessage.getId(), recipientId);
                } catch (IOException e) {
                    log.error("Failed to send message to session {}: {}", recipientSession.getId(), e.getMessage());
                }
            } else {
                log.info("Recipient {} is offline. Message {} saved.", recipientId, savedMessage.getId());
            }
        } catch (Exception e) {
            log.error("Error processing message from session {}: {}", session.getId(), e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            // Get the userId from the session attributes
            String userIdString = (String) session.getAttributes().get("userId");

            // Remove the session from the active sessions map
            if (userIdString != null) {
                UUID userId = UUID.fromString(userIdString);
                sessions.remove(userId);
                log.info("CONNECTION CLOSED: User {} - Status: {}", userId, status.getCode());
            } else {
                // If the session was unauthenticated
                log.info("CONNECTION CLOSED: Session {} (unauthenticated) - Status: {}", session.getId(), status.getCode());
            }
        } catch (Exception e) {
            log.warn("Error while closing session {}: {}", session.getId(), e.getMessage());
        }
    }
}

