package com.bis.assistant.dto;

import java.time.Instant;

public class AuthResponse {

    private boolean success;
    private String token;
    private String tokenType;
    private UserProfileDTO user;
    private String message;
    private String timestamp;

    public AuthResponse() {
        this.timestamp = Instant.now().toString();
        this.tokenType = "Bearer";
    }

    public AuthResponse(boolean success, String token, String tokenType, UserProfileDTO user, String message) {
        this.success = success;
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public static AuthResponse success(String token, UserProfileDTO user, String message) {
        return new AuthResponse(true, token, "Bearer", user, message);
    }

    public static AuthResponse error(String errorMessage) {
        return new AuthResponse(false, null, null, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserProfileDTO getUser() {
        return user;
    }

    public void setUser(UserProfileDTO user) {
        this.user = user;
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
