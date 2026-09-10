package com.bis.assistant.controller;

import com.bis.assistant.dto.ConversationDTO;
import com.bis.assistant.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = {
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:5174", "http://127.0.0.1:5174",
        "http://localhost:5175", "http://127.0.0.1:5175",
        "http://localhost:3000", "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationDTO>> getAllConversations() {
        return ResponseEntity.ok(conversationService.getAllConversations());
    }

    @GetMapping("/{sessionIdentifier}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable("sessionIdentifier") String sessionIdentifier) {
        return conversationService.getConversation(sessionIdentifier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sessionIdentifier}")
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable("sessionIdentifier") String sessionIdentifier) {
        boolean deleted = conversationService.deleteConversation(sessionIdentifier);
        return ResponseEntity.ok(Map.of("deleted", deleted, "sessionIdentifier", sessionIdentifier));
    }
}
