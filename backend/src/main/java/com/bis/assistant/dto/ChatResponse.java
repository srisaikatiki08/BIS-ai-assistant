package com.bis.assistant.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatResponse {

    private boolean success;
    private String answer;
    private List<String> sources = new ArrayList<>();
    private String model;
    private String error;
    private String timestamp;
    private String confidence;
    private String category;
    private Map<String, Object> sourceReference;
    private List<Map<String, Object>> citations = new ArrayList<>();
    private List<String> suggestedFollowUps = new ArrayList<>();

    public ChatResponse() {
        this.timestamp = Instant.now().toString();
    }

    public static ChatResponse success(String answer, String model, List<String> sources) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setAnswer(answer);
        response.setModel(model);
        response.setSources(sources != null ? sources : new ArrayList<>());
        response.setConfidence("Evidence-based");
        response.setCategory("BIS Standards & Regulatory Advisory");
        return response;
    }

    public static ChatResponse error(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        response.setAnswer(null);
        response.setModel("None");
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(Map<String, Object> sourceReference) {
        this.sourceReference = sourceReference;
    }

    public List<Map<String, Object>> getCitations() {
        return citations;
    }

    public void setCitations(List<Map<String, Object>> citations) {
        this.citations = citations != null ? citations : new ArrayList<>();
    }

    public List<String> getSuggestedFollowUps() {
        return suggestedFollowUps;
    }

    public void setSuggestedFollowUps(List<String> suggestedFollowUps) {
        this.suggestedFollowUps = suggestedFollowUps;
    }
}
