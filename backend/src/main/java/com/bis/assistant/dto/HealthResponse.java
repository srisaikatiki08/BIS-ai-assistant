package com.bis.assistant.dto;

import java.time.Instant;

public class HealthResponse {

    private String status;
    private boolean databaseConnected;
    private String databaseType;
    private boolean geminiConfigured;
    private String activeModel;
    private String geminiEndpoint;
    private String version;
    private String timestamp;

    public HealthResponse() {
        this.timestamp = Instant.now().toString();
    }

    public HealthResponse(String status, boolean databaseConnected, String databaseType,
                          boolean geminiConfigured, String activeModel, String geminiEndpoint, String version) {
        this.status = status;
        this.databaseConnected = databaseConnected;
        this.databaseType = databaseType;
        this.geminiConfigured = geminiConfigured;
        this.activeModel = activeModel;
        this.geminiEndpoint = geminiEndpoint;
        this.version = version;
        this.timestamp = Instant.now().toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDatabaseConnected() {
        return databaseConnected;
    }

    public void setDatabaseConnected(boolean databaseConnected) {
        this.databaseConnected = databaseConnected;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public boolean isGeminiConfigured() {
        return geminiConfigured;
    }

    public void setGeminiConfigured(boolean geminiConfigured) {
        this.geminiConfigured = geminiConfigured;
    }

    public String getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(String activeModel) {
        this.activeModel = activeModel;
    }

    public String getGeminiEndpoint() {
        return geminiEndpoint;
    }

    public void setGeminiEndpoint(String geminiEndpoint) {
        this.geminiEndpoint = geminiEndpoint;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
