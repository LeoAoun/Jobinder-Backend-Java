package br.com.jobinder.chatservice.controller;

import br.com.jobinder.chatservice.dto.message.MessageDTO;
import br.com.jobinder.chatservice.service.ConversationService;
import br.com.jobinder.chatservice.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Endpoints for retrieving chat messages")
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    @Operation(summary = "Get messages by Conversation ID",
            description = "Retrieves all messages for a specific conversation, sorted by sent date. " +
                    "Users can only retrieve messages from conversations they are part of. Admins can retrieve any.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (user is not part of this conversation)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Conversation not found",
                    content = @Content)
    })
    @PreAuthorize("@conversationService.isUserInConversation(#conversationId, authentication.name) or hasRole('ADMIN')")
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByConversationId(@PathVariable UUID conversationId) {
        List<MessageDTO> messages = messageService.findMessagesDTOByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "Delete all messages in a conversation",
            description = "Deletes the entire message history of a specific conversation. "
                    + "Only administrators are allowed to perform this action.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Messages deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Conversation not found",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> deleteMessages(@PathVariable UUID conversationId) {
        messageService.deleteMessagesByConversationId(conversationId);
        return ResponseEntity.noContent().build();
    }
}