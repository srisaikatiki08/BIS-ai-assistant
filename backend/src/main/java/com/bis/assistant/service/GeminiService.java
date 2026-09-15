package com.bis.assistant.service;

import com.bis.assistant.dto.ChatRequest;
import com.bis.assistant.dto.ChatResponse;
import com.bis.assistant.dto.GeminiModelResult;
import com.bis.assistant.model.KnowledgeChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * High-level AI service orchestrating BIS knowledge retrieval (RAG),
 * grounded prompt synthesis, multi-model fallback execution, and regulatory citation extraction.
 */
@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    private final BisKnowledgeService bisKnowledgeService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final GeminiModelRouter geminiModelRouter;
    private final GeminiClient geminiClient;

    @org.springframework.beans.factory.annotation.Autowired
    public GeminiService(BisKnowledgeService bisKnowledgeService,
                         KnowledgeRetrievalService knowledgeRetrievalService,
                         GeminiModelRouter geminiModelRouter,
                         GeminiClient geminiClient) {
        this.bisKnowledgeService = bisKnowledgeService;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.geminiClient = geminiClient != null ? geminiClient : new GeminiClient();
        this.geminiModelRouter = geminiModelRouter != null ? geminiModelRouter : new GeminiModelRouter(this.geminiClient);
    }

    /**
     * Legacy constructor for backward compatibility with existing unit tests.
     */
    public GeminiService(BisKnowledgeService bisKnowledgeService,
                         KnowledgeRetrievalService knowledgeRetrievalService) {
        this(bisKnowledgeService, knowledgeRetrievalService, null, null);
    }

    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("your_gemini_api_key");
    }

    public String getActiveModel() {
        return geminiModelRouter.getPrimaryModel();
    }

    public String getNormalizedModelName() {
        return geminiModelRouter.getPrimaryModel();
    }

    public List<String> getConfiguredModels() {
        return geminiModelRouter.getConfiguredModels();
    }

    public String getApiUrl() {
        String url = (apiUrl != null && !apiUrl.trim().isEmpty()) ? apiUrl.trim() : "https://generativelanguage.googleapis.com/v1beta/models";
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String getSafeEndpointUrl() {
        return String.format("%s/%s:generateContent", getApiUrl(), getNormalizedModelName());
    }

    /**
     * Generates a BIS compliant answer with RAG knowledge grounding and automated multi-model fallback.
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

        // Step 1: Retrieve BIS Knowledge Chunks once
        List<KnowledgeChunk> chunks = knowledgeRetrievalService.retrieveRelevantChunks(userMessage);
        logger.info("Retrieved {} BIS knowledge chunks for query: {}", chunks.size(), userMessage);

        String knowledgeContext = knowledgeRetrievalService.buildKnowledgeContext(chunks);

        // Step 2: Build grounded system prompt once
        String baseSystemPrompt = bisKnowledgeService.buildSystemPrompt(language);
        String systemPrompt = baseSystemPrompt + "\n\n"
                + "You must use the retrieved BIS knowledge below as the primary factual source for the answer. Do not invent standards, clauses, test values, dates, or regulatory requirements. If the retrieved knowledge does not contain enough information, clearly say that the local knowledge base does not contain enough information instead of making up details.\n\n"
                + "RETRIEVED BIS KNOWLEDGE FROM POSTGRESQL:\n"
                + knowledgeContext;

        // Step 3: Construct the conversational payload once
        Map<String, Object> payload = buildGeminiPayload(systemPrompt, userMessage.trim(), request.getHistory());

        // Step 4: Execute query through GeminiModelRouter with automated fallback
        GeminiModelResult modelResult = geminiModelRouter.executeWithFallback(payload, apiKey.trim(), getApiUrl());

        if (modelResult.isSuccess() && modelResult.getText() != null) {
            String text = modelResult.getText();
            String successfulModel = modelResult.getModelName();

            // Step 5: Extract citations from the generated answer
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

            ChatResponse chatResponse = ChatResponse.success(text.trim(), "Google Gemini (" + successfulModel + ")", sources);
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
        } else {
            return ChatResponse.error(modelResult.getErrorMessage());
        }
    }

    /**
     * Test Gemini connectivity with fallback models diagnostics.
     */
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> models = getConfiguredModels();
        String primaryModel = getActiveModel();

        result.put("primaryModel", primaryModel);
        result.put("configuredModels", models);
        result.put("endpointUrl", getSafeEndpointUrl());
        result.put("apiKeyConfigured", isApiKeyConfigured());

        if (!isApiKeyConfigured()) {
            result.put("connected", false);
            result.put("message", "GEMINI_API_KEY is not configured on the server.");
            return result;
        }

        // Test primary model
        Map<String, Object> primaryTest = geminiClient.testModelConnection(primaryModel, apiKey.trim(), getApiUrl());
        boolean connected = Boolean.TRUE.equals(primaryTest.get("connected"));
        result.put("primaryTest", primaryTest);
        result.put("connected", connected);
        result.put("httpStatus", primaryTest.get("httpStatus"));

        if (connected) {
            result.put("response", primaryTest.get("response"));
            result.put("activeModel", primaryModel);
        } else {
            result.put("errorDetail", primaryTest.get("errorDetail"));

            // If primary model failed with retryable error, check fallback models
            if (models.size() > 1) {
                for (int i = 1; i < models.size(); i++) {
                    String fallbackModel = models.get(i);
                    Map<String, Object> fallbackTest = geminiClient.testModelConnection(fallbackModel, apiKey.trim(), getApiUrl());
                    if (Boolean.TRUE.equals(fallbackTest.get("connected"))) {
                        result.put("fallbackSuccess", true);
                        result.put("fallbackModelUsed", fallbackModel);
                        result.put("connected", true);
                        result.put("response", fallbackTest.get("response"));
                        break;
                    }
                }
            }
        }

        // Optionally discover available models from API
        try {
            List<String> discovered = geminiClient.discoverAvailableModels(apiKey.trim(), getApiUrl());
            if (!discovered.isEmpty()) {
                result.put("discoveredGenerateContentModels", discovered);
            }
        } catch (Exception ignored) {
        }

        return result;
    }

    /**
     * Constructs the Gemini payload with system instruction and multi-turn conversational history.
     */
    public Map<String, Object> buildGeminiPayload(String systemPrompt, String userMessage, List<Map<String, Object>> history) {
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
