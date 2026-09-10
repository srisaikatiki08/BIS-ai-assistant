package com.bis.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private String language = "en";

    private String sessionId;

    private List<Map<String, Object>> history = new ArrayList<>();

    public ChatRequest() {
    }

    public ChatRequest(String message, String language, List<Map<String, Object>> history) {
        this.message = message;
        this.language = (language != null && !language.trim().isEmpty()) ? language : "en";
        this.history = (history != null) ? history : new ArrayList<>();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = (language != null && !language.trim().isEmpty()) ? language : "en";
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Map<String, Object>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, Object>> history) {
        this.history = history;
    }
}
