package br.com.jobinder.chatservice.controller;

import br.com.jobinder.chatservice.dto.conversation.ConversationDTO;
import br.com.jobinder.chatservice.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getConversationsByUserId(@PathVariable UUID userId) {
        List<ConversationDTO> conversations = conversationService.findConversationDTOsByUserId(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDTO> getConversationById(@PathVariable UUID conversationId) {
        ConversationDTO conversation = conversationService.findConversationDTOById(conversationId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<ConversationDTO>> getAllConversations() {
        List<ConversationDTO> conversations = conversationService.findAllConversationDTOs();
        return ResponseEntity.ok(conversations);
    }
}