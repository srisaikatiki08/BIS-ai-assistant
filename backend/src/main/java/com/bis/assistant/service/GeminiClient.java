package com.bis.assistant.service;

import com.bis.assistant.dto.GeminiModelResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

/**
 * Low-level HTTP client for Google Gemini REST API.
 * Handles timeouts, payload transport, JSON parsing, error classification, and model discovery.
 */
@Component
public class GeminiClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());

        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    public GeminiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    /**
     * Calls Google Gemini generateContent endpoint for a specific model.
     *
     * @param modelName  The normalized model identifier (e.g., "gemini-3-flash-preview")
     * @param payload    The JSON-ready payload containing systemInstruction, contents, generationConfig
     * @param apiKey     The Google Gemini API Key
     * @param baseUrl    The base Gemini API URL (e.g., "https://generativelanguage.googleapis.com/v1beta/models")
     * @return GeminiModelResult with success status, generated text, or classified error details
     */
    public GeminiModelResult callGenerateContent(String modelName, Map<String, Object> payload, String apiKey, String baseUrl) {
        String normalizedModel = normalizeModelName(modelName);
        String cleanBaseUrl = cleanBaseUrl(baseUrl);
        String requestUrl = String.format("%s/%s:generateContent?key=%s", cleanBaseUrl, normalizedModel, apiKey.trim());
        String safeEndpointUrl = String.format("%s/%s:generateContent", cleanBaseUrl, normalizedModel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey.trim());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            logger.debug("Executing Gemini generateContent request to: {}", safeEndpointUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode candidates = rootNode.path("candidates");

                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode parts = candidates.get(0).path("content").path("parts");
                    if (parts.isArray() && parts.size() > 0) {
                        String text = parts.get(0).path("text").asText();
                        if (text != null && !text.trim().isEmpty()) {
                            return GeminiModelResult.success(text.trim(), normalizedModel);
                        }
                    }
                }
                return GeminiModelResult.failure(
                        normalizedModel,
                        response.getStatusCode().value(),
                        "Gemini API returned an empty response candidate.",
                        true, // Retryable: another model may generate valid candidates
                        false
                );
            } else {
                return GeminiModelResult.failure(
                        normalizedModel,
                        response.getStatusCode().value(),
                        "Unexpected response status from Gemini API: " + response.getStatusCode(),
                        response.getStatusCode().value() >= 500,
                        false
                );
            }
        } catch (HttpStatusCodeException httpEx) {
            int statusCode = httpEx.getStatusCode().value();
            String errorDetail = extractGoogleErrorMessage(httpEx, apiKey);

            // Error classification
            boolean authError = (statusCode == HttpStatus.UNAUTHORIZED.value() || statusCode == HttpStatus.FORBIDDEN.value());
            boolean isQuotaOrRateLimit = (statusCode == HttpStatus.TOO_MANY_REQUESTS.value() || errorDetail.toLowerCase().contains("quota") || errorDetail.toLowerCase().contains("resource_exhausted"));
            boolean isServerOrUnavailable = (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value() || statusCode == HttpStatus.BAD_GATEWAY.value() || statusCode == HttpStatus.GATEWAY_TIMEOUT.value() || statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value());
            boolean isModelNotFound = (statusCode == HttpStatus.NOT_FOUND.value()); // e.g. model name not enabled in project

            boolean retryable = !authError && (isQuotaOrRateLimit || isServerOrUnavailable || isModelNotFound);

            return GeminiModelResult.failure(
                    normalizedModel,
                    statusCode,
                    "Google Gemini API error (HTTP " + statusCode + "): " + errorDetail,
                    retryable,
                    authError
            );
        } catch (ResourceAccessException timeoutEx) {
            String cleanMsg = sanitize(timeoutEx.getMessage(), apiKey);
            logger.warn("Network timeout/connectivity error communicating with Gemini model [{}]: {}", normalizedModel, cleanMsg);

            return GeminiModelResult.failure(
                    normalizedModel,
                    0,
                    "Connection timeout or network failure reaching Gemini model: " + cleanMsg,
                    true, // Always retryable across other models/network attempts
                    false
            );
        } catch (Exception ex) {
            String cleanMsg = sanitize(ex.getMessage(), apiKey);
            logger.warn("Unexpected exception invoking Gemini model [{}]: {}", normalizedModel, cleanMsg);

            return GeminiModelResult.failure(
                    normalizedModel,
                    0,
                    "Failed to communicate with Google Gemini API: " + cleanMsg,
                    true,
                    false
            );
        }
    }

    /**
     * Tests connectivity of a single model with a minimal test payload.
     */
    public Map<String, Object> testModelConnection(String modelName, String apiKey, String baseUrl) {
        Map<String, Object> result = new LinkedHashMap<>();
        String normalizedModel = normalizeModelName(modelName);
        String cleanBaseUrl = cleanBaseUrl(baseUrl);
        result.put("model", normalizedModel);
        result.put("endpoint", String.format("%s/%s:generateContent", cleanBaseUrl, normalizedModel));

        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", "Respond with exact words: 'Gemini connection verified.'")))
                )
        );

        GeminiModelResult modelResult = callGenerateContent(normalizedModel, payload, apiKey, cleanBaseUrl);
        result.put("connected", modelResult.isSuccess());
        result.put("httpStatus", modelResult.getHttpStatus());

        if (modelResult.isSuccess()) {
            result.put("response", modelResult.getText());
        } else {
            result.put("errorDetail", modelResult.getErrorMessage());
            result.put("retryable", modelResult.isRetryable());
            result.put("authError", modelResult.isAuthError());
        }
        return result;
    }

    /**
     * Safely queries Google Gemini API's model catalog (GET /v1beta/models)
     * and filters for models supporting "generateContent".
     */
    public List<String> discoverAvailableModels(String apiKey, String baseUrl) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String cleanBaseUrl = cleanBaseUrl(baseUrl);
        String requestUrl = String.format("%s?key=%s", cleanBaseUrl, apiKey.trim());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("x-goog-api-key", apiKey.trim());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode modelsArray = rootNode.path("models");

                List<String> validModels = new ArrayList<>();
                if (modelsArray.isArray()) {
                    for (JsonNode modelNode : modelsArray) {
                        String name = modelNode.path("name").asText(); // e.g. "models/gemini-2.5-flash"
                        JsonNode supportedMethods = modelNode.path("supportedGenerationMethods");

                        boolean supportsGenerateContent = false;
                        if (supportedMethods.isArray()) {
                            for (JsonNode method : supportedMethods) {
                                if ("generateContent".equalsIgnoreCase(method.asText())) {
                                    supportsGenerateContent = true;
                                    break;
                                }
                            }
                        }

                        if (supportsGenerateContent && name != null && !name.isBlank()) {
                            validModels.add(normalizeModelName(name));
                        }
                    }
                }
                return validModels;
            }
        } catch (Exception ex) {
            logger.warn("Could not discover available models from Google Gemini API: {}", sanitize(ex.getMessage(), apiKey));
        }
        return Collections.emptyList();
    }

    public String normalizeModelName(String model) {
        if (model == null || model.trim().isEmpty()) {
            return "gemini-3.6-flash";
        }
        String clean = model.trim();
        if (clean.startsWith("models/")) {
            return clean.substring("models/".length());
        }
        return clean;
    }

    private String cleanBaseUrl(String url) {
        String clean = (url != null && !url.trim().isEmpty()) ? url.trim() : "https://generativelanguage.googleapis.com/v1beta/models";
        if (clean.endsWith("/")) {
            clean = clean.substring(0, clean.length() - 1);
        }
        return clean;
    }

    private String extractGoogleErrorMessage(HttpStatusCodeException httpEx, String apiKey) {
        try {
            String rawBody = httpEx.getResponseBodyAsString();
            if (rawBody != null && !rawBody.trim().isEmpty()) {
                JsonNode errNode = objectMapper.readTree(rawBody);
                String msg = errNode.path("error").path("message").asText();
                if (msg != null && !msg.trim().isEmpty()) {
                    return sanitize(msg, apiKey);
                }
            }
        } catch (Exception ignored) {
        }
        return sanitize(httpEx.getMessage(), apiKey);
    }

    private String sanitize(String rawMessage, String apiKey) {
        if (rawMessage == null) return "Unknown error";
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return rawMessage.replace(apiKey.trim(), "[REDACTED_API_KEY]");
        }
        return rawMessage;
    }
}
