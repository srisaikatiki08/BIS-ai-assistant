package com.bis.assistant.dto;

import java.time.Instant;

public class KnowledgeUploadResponse {

    private boolean success;
    private String document;
    private int chunksCreated;
    private int totalPages;
    private String message;
    private String timestamp;

    public KnowledgeUploadResponse() {
        this.timestamp = Instant.now().toString();
    }

    public KnowledgeUploadResponse(boolean success, String document, int chunksCreated, int totalPages, String message) {
        this.success = success;
        this.document = document;
        this.chunksCreated = chunksCreated;
        this.totalPages = totalPages;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public static KnowledgeUploadResponse success(String document, int chunksCreated, int totalPages, String message) {
        return new KnowledgeUploadResponse(true, document, chunksCreated, totalPages, message);
    }

    public static KnowledgeUploadResponse error(String document, String errorMessage) {
        return new KnowledgeUploadResponse(false, document, 0, 0, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public int getChunksCreated() {
        return chunksCreated;
    }

    public void setChunksCreated(int chunksCreated) {
        this.chunksCreated = chunksCreated;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
