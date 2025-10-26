package br.com.jobinder.chatservice.listener;

import br.com.jobinder.chatservice.domain.conversation.Conversation;
import br.com.jobinder.chatservice.dto.MatchCreatedEvent;
import br.com.jobinder.chatservice.service.ConversationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchListener {

    @Autowired
    private ConversationService conversationService;

    @RabbitListener(queues = "${rabbitmq.queues.match-created}")
    public void onMatchCreated(MatchCreatedEvent event) {
        // When a match is created, create a conversation between the users
        Conversation conversation = conversationService.createConversation(event);
    }
}