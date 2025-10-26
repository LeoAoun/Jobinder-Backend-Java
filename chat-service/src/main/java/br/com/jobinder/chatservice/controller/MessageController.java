package br.com.jobinder.chatservice.controller;

import br.com.jobinder.chatservice.dto.message.MessageDTO;
import br.com.jobinder.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByConversationId(@PathVariable UUID conversationId) {
        List<MessageDTO> messages = messageService.findMessagesDTOByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }
}