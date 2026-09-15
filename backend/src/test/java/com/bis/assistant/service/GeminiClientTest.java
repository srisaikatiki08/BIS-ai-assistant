package com.bis.assistant.service;

import com.bis.assistant.dto.GeminiModelResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GeminiClient geminiClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        geminiClient = new GeminiClient(restTemplate, objectMapper);
    }

    @Test
    @DisplayName("Parses valid 200 OK candidate response")
    void parsesSuccessfulResponse() {
        String json = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "IS 4151 covers helmets." }
                        ]
                      }
                    }
                  ]
                }
                """;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getText()).isEqualTo("IS 4151 covers helmets.");
        assertThat(result.getModelName()).isEqualTo("gemini-3-flash-preview");
    }

    @Test
    @DisplayName("Classifies HTTP 503 (Overloaded) as retryable")
    void classifies503AsRetryable() {
        byte[] body = "{\"error\":{\"message\":\"The model is overloaded.\"}}".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", body, StandardCharsets.UTF_8));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isTrue();
        assertThat(result.isAuthError()).isFalse();
        assertThat(result.getHttpStatus()).isEqualTo(503);
        assertThat(result.getErrorMessage()).contains("The model is overloaded.");
    }

    @Test
    @DisplayName("Classifies HTTP 429 (Rate limit / Quota) as retryable")
    void classifies429AsRetryable() {
        byte[] body = "{\"error\":{\"message\":\"Resource has been exhausted.\"}}".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", body, StandardCharsets.UTF_8));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isTrue();
        assertThat(result.isAuthError()).isFalse();
        assertThat(result.getHttpStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("Classifies HTTP 401/403 as authError (non-retryable fail-fast)")
    void classifies401AsAuthError() {
        byte[] body = "{\"error\":{\"message\":\"API_KEY_INVALID\"}}".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized", body, StandardCharsets.UTF_8));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isAuthError()).isTrue();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.getHttpStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Classifies HTTP 400 Bad Request as non-retryable")
    void classifies400AsNonRetryable() {
        byte[] body = "{\"error\":{\"message\":\"Invalid JSON payload.\"}}".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", body, StandardCharsets.UTF_8));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isFalse();
        assertThat(result.isAuthError()).isFalse();
        assertThat(result.getHttpStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Classifies network timeouts as retryable")
    void classifiesTimeoutsAsRetryable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Read timed out"));

        GeminiModelResult result = geminiClient.callGenerateContent("gemini-3-flash-preview", Map.of(), "key", "https://api");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isRetryable()).isTrue();
        assertThat(result.isAuthError()).isFalse();
        assertThat(result.getErrorMessage()).contains("timed out");
    }

    @Test
    @DisplayName("Discovers models and filters for generateContent compatibility")
    void discoversAndFiltersModels() {
        String json = """
                {
                  "models": [
                    {
                      "name": "models/gemini-2.5-flash",
                      "supportedGenerationMethods": ["generateContent", "countTokens"]
                    },
                    {
                      "name": "models/text-embedding-004",
                      "supportedGenerationMethods": ["embedContent"]
                    },
                    {
                      "name": "models/gemini-2.5-pro",
                      "supportedGenerationMethods": ["generateContent"]
                    }
                  ]
                }
                """;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        List<String> discovered = geminiClient.discoverAvailableModels("key", "https://api");

        assertThat(discovered).containsExactly("gemini-2.5-flash", "gemini-2.5-pro");
    }
}
