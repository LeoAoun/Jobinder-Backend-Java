package br.com.jobinder.chatservice.handler;

import br.com.jobinder.chatservice.domain.conversation.Conversation;
import br.com.jobinder.chatservice.domain.message.Message;
import br.com.jobinder.chatservice.dto.WebSocketMessageDTO;
import br.com.jobinder.chatservice.service.ConversationService;
import br.com.jobinder.chatservice.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Map to store active WebSocket sessions with userId as the key
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Retrieve userId from session attributes and store the session
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));
        sessions.put(userId, session);
        System.out.println("NEW CONNECTION: " + userId + " WITH SESSION ID: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Convert the incoming message payload to WebSocketMessageDTO
        WebSocketMessageDTO messageDto = objectMapper.readValue(message.getPayload(), WebSocketMessageDTO.class);

        Message savedMessage = messageService.saveMessage(messageDto);

        // Determine the recipient based on the conversation
        Conversation conversation = savedMessage.getConversation();
        UUID recipientId = conversation.getClientUserId().equals(savedMessage.getSenderId())
                ? conversation.getProfessionalUserId()
                : conversation.getClientUserId();

        // Send the message to the recipient if they are connected
        WebSocketSession recipientSession = sessions.get(recipientId);
        if (recipientSession != null && recipientSession.isOpen()) {
            try {
                recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(savedMessage)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));
        sessions.remove(userId);
        System.out.println("CLOSED CONNECTION: " + userId + " - Status: " + status.getCode());
    }
}