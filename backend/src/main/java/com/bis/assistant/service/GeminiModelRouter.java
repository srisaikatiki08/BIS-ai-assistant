package com.bis.assistant.service;

import com.bis.assistant.dto.GeminiModelResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Manages the ordered fallback list of Gemini models and executes failover logic.
 * Ensures transparent logging and prevents retry loops.
 */
@Service
public class GeminiModelRouter {

    private static final Logger logger = LoggerFactory.getLogger(GeminiModelRouter.class);

    private static final List<String> DEFAULT_FALLBACK_MODELS = List.of(
            "gemini-3.6-flash",
            "gemini-3.8-flash",
            "gemini-3.7-flash",
            "gemini-3-flash-preview",
            "gemini-3.5-flash-lite",
            "gemini-2.5-flash-lite"
    );

    @Value("${gemini.api.models:}")
    private String configuredModelsString;

    @Value("${gemini.api.model:gemini-3.6-flash}")
    private String legacyModel;

    private final GeminiClient geminiClient;

    @org.springframework.beans.factory.annotation.Autowired
    public GeminiModelRouter(GeminiClient geminiClient) {
        this.geminiClient = geminiClient != null ? geminiClient : new GeminiClient();
    }

    public GeminiModelRouter(GeminiClient geminiClient, String configuredModelsString) {
        this.geminiClient = geminiClient != null ? geminiClient : new GeminiClient();
        this.configuredModelsString = configuredModelsString;
    }

    /**
     * Resolves the ordered, deduplicated list of configured Gemini models.
     * Precedence: GEMINI_MODELS (comma-separated) -> GEMINI_MODEL (single model) -> Defaults.
     */
    public List<String> getConfiguredModels() {
        Set<String> resolved = new LinkedHashSet<>();

        if (configuredModelsString != null && !configuredModelsString.trim().isEmpty()) {
            String[] tokens = configuredModelsString.split(",");
            for (String token : tokens) {
                String clean = normalizeModelName(token);
                if (!clean.isEmpty()) {
                    resolved.add(clean);
                }
            }
        }

        if (resolved.isEmpty() && legacyModel != null && !legacyModel.trim().isEmpty()) {
            String clean = normalizeModelName(legacyModel);
            if (!clean.isEmpty()) {
                resolved.add(clean);
            }
        }

        if (resolved.isEmpty()) {
            resolved.addAll(DEFAULT_FALLBACK_MODELS);
        }

        return new ArrayList<>(resolved);
    }

    public static String normalizeModelName(String model) {
        if (model == null || model.trim().isEmpty()) {
            return "";
        }
        String clean = model.trim();
        if (clean.startsWith("models/")) {
            return clean.substring("models/".length());
        }
        return clean;
    }

    /**
     * Returns the primary (first-choice) model.
     */
    public String getPrimaryModel() {
        List<String> models = getConfiguredModels();
        return models.isEmpty() ? "gemini-3.6-flash" : models.get(0);
    }

    /**
     * Executes the Gemini request with ordered model fallback.
     *
     * @param payload Pre-constructed system prompt and conversation payload
     * @param apiKey  Google Gemini API key
     * @param baseUrl Base URL for Gemini API
     * @return GeminiModelResult with successful generation or aggregated failure description
     */
    public GeminiModelResult executeWithFallback(Map<String, Object> payload, String apiKey, String baseUrl) {
        List<String> models = getConfiguredModels();
        int totalModels = models.size();
        GeminiModelResult lastResult = null;

        for (int i = 0; i < totalModels; i++) {
            String currentModel = models.get(i);
            int attemptNum = i + 1;

            logger.info("Trying Gemini model: [{}] ({}/{})", currentModel, attemptNum, totalModels);

            GeminiModelResult result = geminiClient.callGenerateContent(currentModel, payload, apiKey, baseUrl);

            if (result.isSuccess()) {
                if (i > 0) {
                    logger.info("Gemini fallback succeeded using model: [{}] (after {} prior model failure(s))", currentModel, i);
                } else {
                    logger.info("Gemini model [{}] succeeded.", currentModel);
                }
                return result;
            }

            lastResult = result;

            // 1. Fail-fast on authentication/permission errors (invalid API key applies to all models)
            if (result.isAuthError()) {
                logger.error("Gemini authentication/permission error with model [{}] (HTTP {}): {}. Aborting fallback for remaining models.",
                        currentModel, result.getHttpStatus(), result.getErrorMessage());
                return result;
            }

            // 2. Fail-fast on non-retryable bad requests (e.g. malformed JSON)
            if (!result.isRetryable()) {
                logger.warn("Gemini model [{}] failed with non-retryable error (HTTP {}): {}. Aborting fallback.",
                        currentModel, result.getHttpStatus(), result.getErrorMessage());
                return result;
            }

            // 3. Retryable error: log and proceed to next configured model
            if (attemptNum < totalModels) {
                String nextModel = models.get(attemptNum);
                logger.warn("Gemini model [{}] failed with HTTP {} ({}). Trying fallback model: [{}]...",
                        currentModel, result.getHttpStatus(), result.getErrorMessage(), nextModel);
            } else {
                logger.warn("Gemini model [{}] failed with HTTP {} ({}). No more fallback models configured.",
                        currentModel, result.getHttpStatus(), result.getErrorMessage());
            }
        }

        logger.error("All {} configured Gemini models failed. Models attempted: {}", totalModels, models);

        int lastStatus = (lastResult != null && lastResult.getHttpStatus() > 0) ? lastResult.getHttpStatus() : 503;
        String userFriendlyError = "All configured AI models are temporarily unavailable. Please try again shortly.";

        return GeminiModelResult.failure(
                "all-models",
                lastStatus,
                userFriendlyError,
                false,
                false
        );
    }
}
