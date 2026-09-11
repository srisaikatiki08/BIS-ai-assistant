package com.bis.assistant.service;

import com.bis.assistant.dto.ChatRequest;
import com.bis.assistant.dto.ChatResponse;
import com.bis.assistant.model.KnowledgeChunk;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.5-flash}")
    private String defaultModel;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    private final BisKnowledgeService bisKnowledgeService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService(BisKnowledgeService bisKnowledgeService,
                         KnowledgeRetrievalService knowledgeRetrievalService) {
        this.bisKnowledgeService = bisKnowledgeService;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("your_gemini_api_key");
    }

    public String getActiveModel() {
        return (defaultModel != null && !defaultModel.trim().isEmpty()) ? defaultModel.trim() : "gemini-2.5-flash";
    }

    public String getNormalizedModelName() {
        String model = getActiveModel();
        if (model.startsWith("models/")) {
            return model.substring("models/".length());
        }
        return model;
    }

    public String getApiUrl() {
        String url = (apiUrl != null && !apiUrl.trim().isEmpty()) ? apiUrl.trim() : "https://generativelanguage.googleapis.com/v1beta/models";
        // Remove trailing slash if present
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String getSafeEndpointUrl() {
        return String.format("%s/%s:generateContent", getApiUrl(), getNormalizedModelName());
    }

    /**
     * Generates a BIS compliant answer by calling Google Gemini API with knowledge grounding and conversation history.
     */
    public ChatResponse generateBisAnswer(ChatRequest request) {
        String userMessage = request.getMessage();
        String language = request.getLanguage();

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ChatResponse.error("User message cannot be empty.");
        }

        if (!isApiKeyConfigured()) {
            logger.warn("GEMINI_API_KEY is not configured in environment or properties.");
            return ChatResponse.error("Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable on the Spring Boot server.");
        }

        List<KnowledgeChunk> chunks = knowledgeRetrievalService.retrieveRelevantChunks(userMessage);
        logger.info("Retrieved {} BIS knowledge chunks for query: {}", chunks.size(), userMessage);

        String knowledgeContext = knowledgeRetrievalService.buildKnowledgeContext(chunks);

        String modelName = getNormalizedModelName();
        String baseSystemPrompt = bisKnowledgeService.buildSystemPrompt(language);
        String systemPrompt = baseSystemPrompt + "\n\n"
                + "You must use the retrieved BIS knowledge below as the primary factual source for the answer. Do not invent standards, clauses, test values, dates, or regulatory requirements. If the retrieved knowledge does not contain enough information, clearly say that the local knowledge base does not contain enough information instead of making up details.\n\n"
                + "RETRIEVED BIS KNOWLEDGE FROM POSTGRESQL:\n"
                + knowledgeContext;

        // Prepare Gemini Request Payload with System Instruction and History
        Map<String, Object> payload = buildGeminiPayload(systemPrompt, userMessage.trim(), request.getHistory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey.trim());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        String safeUrl = getSafeEndpointUrl();
        String requestUrl = String.format("%s/%s:generateContent?key=%s", getApiUrl(), modelName, apiKey.trim());

        try {
            logger.info("Executing Google Gemini request to: {}", safeUrl);

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
                            List<Map<String, Object>> citations = extractCitations(text, chunks);

                            // Construct sources list from cited documents (or top chunks if no citations tagged)
                            List<String> sources = !citations.isEmpty()
                                    ? citations.stream()
                                            .map(c -> (String) c.get("document"))
                                            .filter(Objects::nonNull)
                                            .map(String::trim)
                                            .distinct()
                                            .toList()
                                    : chunks.stream()
                                            .map(KnowledgeChunk::getDocument)
                                            .filter(doc -> doc != null && !doc.isBlank())
                                            .map(String::trim)
                                            .distinct()
                                            .toList();

                            ChatResponse chatResponse = ChatResponse.success(text.trim(), "Google Gemini (" + modelName + ")", sources);
                            chatResponse.setCitations(citations);
                            chatResponse.setConfidence(!citations.isEmpty() ? "Evidence-based" : "Standard Advisory");

                            // Populate primary sourceReference from the first cited source
                            Map<String, Object> sourceRef = new HashMap<>();
                            if (!citations.isEmpty()) {
                                Map<String, Object> primary = citations.get(0);
                                sourceRef.putAll(primary);
                                sourceRef.put("disclaimer", "Official Bureau of Indian Standards regulatory reference.");
                            } else if (!chunks.isEmpty()) {
                                KnowledgeChunk firstChunk = chunks.get(0);
                                if (firstChunk.getDocument() != null && !firstChunk.getDocument().isBlank()) {
                                    sourceRef.put("document", firstChunk.getDocument());
                                }
                                if (firstChunk.getSection() != null && !firstChunk.getSection().isBlank()) {
                                    sourceRef.put("section", firstChunk.getSection());
                                }
                                if (firstChunk.getClause() != null && !firstChunk.getClause().isBlank()) {
                                    sourceRef.put("clause", firstChunk.getClause());
                                }
                                if (firstChunk.getPageNumber() != null) {
                                    sourceRef.put("pageNumber", firstChunk.getPageNumber());
                                }
                                if (firstChunk.getSourceUrl() != null && !firstChunk.getSourceUrl().isBlank()) {
                                    sourceRef.put("sourceUrl", firstChunk.getSourceUrl().trim());
                                    sourceRef.put("portalUrl", firstChunk.getSourceUrl().trim());
                                }
                                sourceRef.put("disclaimer", "Official Bureau of Indian Standards regulatory reference.");
                            }
                            chatResponse.setSourceReference(sourceRef);

                            chatResponse.setSuggestedFollowUps(List.of(
                                    "What are the laboratory testing fees and timeline?",
                                    "Which documents are required for MSME fee concession?",
                                    "Find accredited testing laboratories nearby",
                                    "Is this standard under a mandatory QCO in 2026?"
                            ));

                            return chatResponse;
                        }
                    }
                }
                return ChatResponse.error("Gemini API returned an empty response candidate.");
            } else {
                return ChatResponse.error("Unexpected response status from Gemini API: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException httpEx) {
            String errorDetail = extractGoogleErrorMessage(httpEx);
            int statusCode = httpEx.getStatusCode().value();
            logger.error("Gemini API call failed with HTTP {} at {}: {}", statusCode, safeUrl, errorDetail);

            return ChatResponse.error("Google Gemini API error (HTTP " + statusCode + "): " + errorDetail);
        } catch (Exception ex) {
            String cleanMsg = sanitizeErrorMessage(ex.getMessage());
            logger.error("Exception during Gemini API invocation at {}: {}", safeUrl, cleanMsg, ex);
            return ChatResponse.error("Failed to connect to Google Gemini API: " + cleanMsg);
        }
    }

    /**
     * Test Gemini connectivity with a minimal prompt and safe diagnostics.
     */
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("configuredModel", getActiveModel());
        result.put("normalizedModel", getNormalizedModelName());
        result.put("endpointUrl", getSafeEndpointUrl());
        result.put("apiKeyConfigured", isApiKeyConfigured());

        if (!isApiKeyConfigured()) {
            result.put("connected", false);
            result.put("message", "GEMINI_API_KEY is not configured on the server.");
            return result;
        }

        String model = getNormalizedModelName();
        try {
            Map<String, Object> payload = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", "Respond with exact words: 'Gemini connection verified.'")))
                    )
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey.trim());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String requestUrl = String.format("%s/%s:generateContent?key=%s", getApiUrl(), model, apiKey.trim());
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);

            result.put("httpStatus", response.getStatusCode().value());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String text = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
                result.put("connected", true);
                result.put("response", text != null && !text.isEmpty() ? text.trim() : "Connected");
            } else {
                result.put("connected", false);
                result.put("message", "Non-200 response from Gemini API: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException httpEx) {
            result.put("connected", false);
            result.put("httpStatus", httpEx.getStatusCode().value());
            result.put("errorDetail", extractGoogleErrorMessage(httpEx));
        } catch (Exception e) {
            result.put("connected", false);
            result.put("errorDetail", sanitizeErrorMessage(e.getMessage()));
        }
        return result;
    }

    private String extractGoogleErrorMessage(HttpStatusCodeException httpEx) {
        try {
            String rawBody = httpEx.getResponseBodyAsString();
            if (rawBody != null && !rawBody.trim().isEmpty()) {
                JsonNode errNode = objectMapper.readTree(rawBody);
                String msg = errNode.path("error").path("message").asText();
                if (msg != null && !msg.trim().isEmpty()) {
                    return sanitizeErrorMessage(msg);
                }
            }
        } catch (Exception ignored) {
        }
        return sanitizeErrorMessage(httpEx.getMessage());
    }

    /**
     * Constructs the Gemini payload with system instruction and multi-turn conversational history.
     */
    private Map<String, Object> buildGeminiPayload(String systemPrompt, String userMessage, List<Map<String, Object>> history) {
        Map<String, Object> payload = new HashMap<>();

        // 1. System Instruction
        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPrompt))
        );
        payload.put("systemInstruction", systemInstruction);

        // 2. Multi-turn Conversational Contents
        List<Map<String, Object>> contents = new ArrayList<>();

        if (history != null && !history.isEmpty()) {
            for (Map<String, Object> msg : history) {
                String role = (String) msg.get("role");
                String text = (String) msg.get("content");
                if (text == null) text = (String) msg.get("text");

                if (text == null || text.trim().isEmpty()) continue;

                // Skip initial static system greetings that didn't come from user interaction
                Object idObj = msg.get("id");
                if (idObj != null && (idObj.toString().contains("initial-welcome") || idObj.toString().contains("welcome-"))) {
                    continue;
                }

                String geminiRole = "user".equalsIgnoreCase(role) ? "user" : "model";

                // Ensure turns alternate cleanly in Gemini
                if (!contents.isEmpty() && contents.get(contents.size() - 1).get("role").equals(geminiRole)) {
                    // Append text if consecutive same role
                    Map<String, Object> lastTurn = contents.get(contents.size() - 1);
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = new ArrayList<>((List<Map<String, Object>>) lastTurn.get("parts"));
                    parts.add(Map.of("text", text.trim()));
                    lastTurn.put("parts", parts);
                } else {
                    contents.add(new HashMap<>(Map.of(
                            "role", geminiRole,
                            "parts", new ArrayList<>(List.of(Map.of("text", text.trim())))
                    )));
                }
            }
        }

        // 3. Append current user message
        if (!contents.isEmpty() && contents.get(contents.size() - 1).get("role").equals("user")) {
            Map<String, Object> lastTurn = contents.get(contents.size() - 1);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parts = new ArrayList<>((List<Map<String, Object>>) lastTurn.get("parts"));
            parts.add(Map.of("text", userMessage));
            lastTurn.put("parts", parts);
        } else {
            contents.add(new HashMap<>(Map.of(
                    "role", "user",
                    "parts", new ArrayList<>(List.of(Map.of("text", userMessage)))
            )));
        }

        payload.put("contents", contents);

        // 4. Generation Config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.3);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 4096);
        payload.put("generationConfig", generationConfig);

        return payload;
    }

    private String sanitizeErrorMessage(String rawMessage) {
        if (rawMessage == null) return "Unexpected error";
        if (apiKey != null && !apiKey.isEmpty()) {
            return rawMessage.replace(apiKey, "[REDACTED_API_KEY]");
        }
        return rawMessage;
    }

    /**
     * Parses all valid [Source N] references from the generated answer and builds structured citation metadata.
     */
    public List<Map<String, Object>> extractCitations(String answerText, List<KnowledgeChunk> retrievedChunks) {
        if (answerText == null || answerText.isBlank() || retrievedChunks == null || retrievedChunks.isEmpty()) {
            return Collections.emptyList();
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)\\[Source\\s+(\\d+)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(answerText);
        Set<Integer> citedIndices = new LinkedHashSet<>();

        while (matcher.find()) {
            try {
                int sourceNum = Integer.parseInt(matcher.group(1));
                if (sourceNum >= 1 && sourceNum <= retrievedChunks.size()) {
                    citedIndices.add(sourceNum);
                } else {
                    logger.warn("Gemini cited out-of-bounds [Source {}] (retrieved chunks available: {})", sourceNum, retrievedChunks.size());
                }
            } catch (NumberFormatException ignored) {
            }
        }

        List<Map<String, Object>> citations = new ArrayList<>();
        for (int sourceNum : citedIndices) {
            KnowledgeChunk chunk = retrievedChunks.get(sourceNum - 1);
            Map<String, Object> citation = new LinkedHashMap<>();
            citation.put("source", "[Source " + sourceNum + "]");
            if (chunk.getDocument() != null && !chunk.getDocument().isBlank()) {
                citation.put("document", chunk.getDocument());
            }
            if (chunk.getTitle() != null && !chunk.getTitle().isBlank()) {
                citation.put("title", chunk.getTitle());
            }
            if (chunk.getSection() != null && !chunk.getSection().isBlank()) {
                citation.put("section", chunk.getSection());
            }
            if (chunk.getClause() != null && !chunk.getClause().isBlank()) {
                citation.put("clause", chunk.getClause());
            }
            if (chunk.getPageNumber() != null) {
                citation.put("pageNumber", chunk.getPageNumber());
            }
            if (chunk.getSourceUrl() != null && !chunk.getSourceUrl().isBlank()) {
                citation.put("sourceUrl", chunk.getSourceUrl().trim());
                citation.put("portalUrl", chunk.getSourceUrl().trim());
            }
            citations.add(citation);
        }

        return citations;
    }
}
