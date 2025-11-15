package br.com.jobinder.chatservice.service;

import br.com.jobinder.chatservice.domain.conversation.ConversationRepository;
import br.com.jobinder.chatservice.domain.message.Message;
import br.com.jobinder.chatservice.domain.message.MessageRepository;
import br.com.jobinder.chatservice.dto.WebSocketMessageDTO;
import br.com.jobinder.chatservice.dto.message.MessageDTO;
import br.com.jobinder.chatservice.infra.exception.ConversationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    public Message saveMessage(WebSocketMessageDTO messageDto) {
        var conversation = conversationRepository.findById(messageDto.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found with ID: " + messageDto.conversationId()));

        var message = new Message(
                null,
                messageDto.senderId(),
                messageDto.content(),
                null,
                null,
                conversation
        );

        return messageRepository.save(message);
    }

    public List<MessageDTO> findMessagesDTOByConversationId(UUID conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException("Cannot find messages: Conversation not found with ID: " + conversationId);
        }
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(message -> new MessageDTO(
                        message.getId(),
                        message.getSenderId(),
                        message.getContent(),
                        message.getSentAt(),
                        message.getReadAt(),
                        message.getConversation().getId()
                ))
                .toList();
    }

    @Transactional
    public void deleteMessagesByConversationId(UUID conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ConversationNotFoundException("Conversation not found with ID: " + conversationId);
        }
        messageRepository.deleteByConversationId(conversationId);
    }
}