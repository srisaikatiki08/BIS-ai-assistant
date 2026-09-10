package com.bis.assistant.controller;

import com.bis.assistant.dto.ChatRequest;
import com.bis.assistant.dto.ChatResponse;
import com.bis.assistant.dto.HealthResponse;
import com.bis.assistant.service.ConversationService;
import com.bis.assistant.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:5174", "http://127.0.0.1:5174",
        "http://localhost:5175", "http://127.0.0.1:5175",
        "http://localhost:3000", "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class ChatController {

    private final GeminiService geminiService;
    private final ConversationService conversationService;
    private final DataSource dataSource;

    public ChatController(GeminiService geminiService,
                          ConversationService conversationService,
                          DataSource dataSource) {
        this.geminiService = geminiService;
        this.conversationService = conversationService;
        this.dataSource = dataSource;
    }

    /**
     * POST /api/chat
     * Receives message, language, optional sessionId and history, persists user message,
     * queries Google Gemini, persists assistant response, and returns structured result.
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String session = request.getSessionId();
        if (session == null || session.trim().isEmpty()) {
            session = "sess-" + System.currentTimeMillis();
        }

        // Persist User Message in PostgreSQL
        try {
            conversationService.saveMessage(session, "user", request.getMessage());
        } catch (Exception e) {
            // Non-blocking log if DB write fails during transient error
        }

        ChatResponse response = geminiService.generateBisAnswer(request);

        // Persist Assistant Response in PostgreSQL if successful
        if (response.isSuccess() && response.getAnswer() != null) {
            try {
                conversationService.saveMessage(session, "model", response.getAnswer());
            } catch (Exception e) {
                // Non-blocking log
            }
            return ResponseEntity.ok(response);
        } else {
            HttpStatus status = !geminiService.isApiKeyConfigured() ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        }
    }

    /**
     * GET /api/health
     * System health, PostgreSQL database connection, and Gemini configuration check.
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        boolean dbConnected = false;
        String dbType = "PostgreSQL";
        try (Connection conn = dataSource.getConnection()) {
            dbConnected = conn.isValid(2);
            dbType = conn.getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            dbConnected = false;
        }

        boolean geminiConfigured = geminiService.isApiKeyConfigured();

        HealthResponse health = new HealthResponse(
                "UP",
                dbConnected,
                dbType,
                geminiConfigured,
                geminiService.getNormalizedModelName(),
                geminiService.getSafeEndpointUrl(),
                "1.0.0"
        );
        return ResponseEntity.ok(health);
    }

    /**
     * GET /api/gemini/diagnostic
     * Diagnostic endpoint reporting Gemini connection status and safe URL without key.
     */
    @GetMapping("/gemini/diagnostic")
    public ResponseEntity<Map<String, Object>> getGeminiDiagnostic() {
        Map<String, Object> testResult = geminiService.testConnection();
        return ResponseEntity.ok(testResult);
    }

    /**
     * POST /api/test-gemini
     * Test active connection to Gemini API without exposing secret keys.
     */
    @PostMapping("/test-gemini")
    public ResponseEntity<Map<String, Object>> testGemini() {
        Map<String, Object> testResult = geminiService.testConnection();
        return ResponseEntity.ok(testResult);
    }

    /**
     * GET /api/status
     * Simple status confirmation.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of(
                "status", "running",
                "service", "BIS Intelligent Assistant API",
                "database", "PostgreSQL 18",
                "framework", "Spring Boot 3.3.4"
        ));
    }
}
