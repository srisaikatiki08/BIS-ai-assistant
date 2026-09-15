package com.bis.assistant.service;

import com.bis.assistant.dto.ChatRequest;
import com.bis.assistant.dto.ChatResponse;
import com.bis.assistant.dto.GeminiModelResult;
import com.bis.assistant.model.KnowledgeChunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeminiModelFallbackTest {

    private static final List<String> SIX_PRIORITY_MODELS = List.of(
            "gemini-3.6-flash",
            "gemini-3.8-flash",
            "gemini-3.7-flash",
            "gemini-3-flash-preview",
            "gemini-3.5-flash-lite",
            "gemini-2.5-flash-lite"
    );

    @Mock
    private BisKnowledgeService bisKnowledgeService;

    @Mock
    private KnowledgeRetrievalService knowledgeRetrievalService;

    @Mock
    private GeminiClient geminiClient;

    private GeminiModelRouter geminiModelRouter;
    private GeminiService geminiService;

    private KnowledgeChunk sampleChunk;

    @BeforeEach
    void setUp() {
        String modelsConfig = String.join(",", SIX_PRIORITY_MODELS);
        geminiModelRouter = new GeminiModelRouter(geminiClient, modelsConfig);
        geminiService = new GeminiService(bisKnowledgeService, knowledgeRetrievalService, geminiModelRouter, geminiClient);

        ReflectionTestUtils.setField(geminiService, "apiKey", "test-valid-api-key");
        ReflectionTestUtils.setField(geminiService, "apiUrl", "https://generativelanguage.googleapis.com/v1beta/models");

        sampleChunk = new KnowledgeChunk(
                "IS 4151:2015",
                "Motorcycle helmet peak acceleration shall not exceed 300g",
                "Impact Attenuation Requirements",
                "Section 7",
                "Clause 7.1",
                8,
                "https://manakonline.in"
        );
        sampleChunk.setId(1L);

        when(knowledgeRetrievalService.retrieveRelevantChunks(anyString())).thenReturn(List.of(sampleChunk));
        when(knowledgeRetrievalService.buildKnowledgeContext(anyList())).thenReturn("Document: IS 4151:2015 - Helmet peak accel 300g");
        when(bisKnowledgeService.buildSystemPrompt(anyString())).thenReturn("You are BIS Intelligent Assistant.");
    }

    @Test
    @DisplayName("1. First model (gemini-3.6-flash) succeeds -> Returns first model response immediately")
    void firstModelSucceedsImmediately() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("According to IS 4151:2015 [Source 1], helmet impact must not exceed 300g.", "gemini-3.6-flash"));

        ChatRequest request = new ChatRequest("What is IS 4151 helmet acceleration?", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3.6-flash");
        assertThat(response.getAnswer()).contains("300g");
        assertThat(response.getCitations()).hasSize(1);
        assertThat(response.getCitations().get(0).get("document")).isEqualTo("IS 4151:2015");

        // Verify remaining 5 fallback models were NOT attempted
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.5-flash-lite"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-flash-lite"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("2. First model returns HTTP 503 (High Demand) -> Second model (gemini-3.8-flash) is attempted and succeeds")
    void firstModelFails503FallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 503, "The model is currently experiencing high demand.", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("IS 4151:2015 [Source 1] specifies motorcycle protective helmets.", "gemini-3.8-flash"));

        ChatRequest request = new ChatRequest("Explain IS 4151", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3.8-flash");
        assertThat(response.getAnswer()).contains("protective helmets");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("3. First model returns HTTP 429 (Rate Limit / Quota) -> Second model is attempted and succeeds")
    void firstModelFails429FallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 429, "Resource has been exhausted (e.g. check quota).", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Under IS 4151 [Source 1], tests must verify shell integrity.", "gemini-3.8-flash"));

        ChatRequest request = new ChatRequest("Explain helmet requirements", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3.8-flash");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("4. First model times out -> Fallback model is attempted and succeeds")
    void firstModelTimesOutFallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 0, "Connection timeout reaching Gemini endpoint", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Fallback answer verified with IS 4151 [Source 1].", "gemini-3.8-flash"));

        ChatRequest request = new ChatRequest("Helmet impact test query", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3.8-flash");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("5. Several models fail in sequence (1-4 fail), fifth model (gemini-3.5-flash-lite) succeeds")
    void severalModelsFailLaterModelSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.8-flash", 502, "Bad gateway", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.7-flash", 429, "Rate limited", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.5-flash-lite"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Fifth model response for IS 4151 [Source 1].", "gemini-3.5-flash-lite"));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3.5-flash-lite");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.5-flash-lite"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-flash-lite"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("6. All six models fail -> Clean controlled error message returned to user without retry loop")
    void allSixModelsFailReturnsCleanError() {
        for (String model : SIX_PRIORITY_MODELS) {
            when(geminiClient.callGenerateContent(eq(model), anyMap(), anyString(), anyString()))
                    .thenReturn(GeminiModelResult.failure(model, 503, "High demand", true, false));
        }

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).contains("All configured AI models are temporarily unavailable");

        // Confirm each of the 6 models was attempted exactly once
        for (String model : SIX_PRIORITY_MODELS) {
            verify(geminiClient, times(1)).callGenerateContent(eq(model), anyMap(), anyString(), anyString());
        }
    }

    @Test
    @DisplayName("7. Invalid API Key (HTTP 401/403) -> Fail-fast immediately, does not waste attempts on remaining 5 models")
    void invalidApiKeyFailsFastWithoutTryingOtherModels() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 401, "API_KEY_INVALID", false, true));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).contains("API_KEY_INVALID");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.7-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.5-flash-lite"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-flash-lite"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("8. HTTP 400 Bad Request -> Non-retryable permanent error fails fast without trying other models")
    void badRequestFailsFastWithoutTryingOtherModels() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 400, "INVALID_ARGUMENT", false, false));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).contains("INVALID_ARGUMENT");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("9. RAG context is retrieved once and the identical payload is reused across fallback attempts")
    void ragContextConstructedOnceAndReused() {
        when(geminiClient.callGenerateContent(eq("gemini-3.6-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3.6-flash", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-3.8-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Answer from fallback model [Source 1].", "gemini-3.8-flash"));

        ChatRequest request = new ChatRequest("Impact test", "en", null);
        geminiService.generateBisAnswer(request);

        // Verify Knowledge retrieval occurred exactly once
        verify(knowledgeRetrievalService, times(1)).retrieveRelevantChunks("Impact test");
        verify(knowledgeRetrievalService, times(1)).buildKnowledgeContext(anyList());
        verify(bisKnowledgeService, times(1)).buildSystemPrompt("en");

        // Verify the exact same payload was passed to both model attempts
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(geminiClient, times(2)).callGenerateContent(anyString(), payloadCaptor.capture(), anyString(), anyString());

        List<Map<String, Object>> capturedPayloads = payloadCaptor.getAllValues();
        assertThat(capturedPayloads.get(0)).isEqualTo(capturedPayloads.get(1));
    }

    @Test
    @DisplayName("10. Model router verifies exact 6-model priority order and default fallback list")
    void modelRouterVerifiesExactSixModelPriorityOrder() {
        GeminiModelRouter router = new GeminiModelRouter(geminiClient, "");
        List<String> models = router.getConfiguredModels();

        assertThat(models).containsExactly(
                "gemini-3.6-flash",
                "gemini-3.8-flash",
                "gemini-3.7-flash",
                "gemini-3-flash-preview",
                "gemini-3.5-flash-lite",
                "gemini-2.5-flash-lite"
        );
        assertThat(router.getPrimaryModel()).isEqualTo("gemini-3.6-flash");
    }

    @Test
    @DisplayName("11. GEMINI_MODELS environment variable overrides the default model list and deduplicates")
    void geminiModelsEnvVarOverridesDefaultList() {
        GeminiModelRouter router = new GeminiModelRouter(geminiClient, " models/custom-model-1, custom-model-2 , custom-model-1 ");
        List<String> models = router.getConfiguredModels();

        assertThat(models).containsExactly("custom-model-1", "custom-model-2");
        assertThat(router.getPrimaryModel()).isEqualTo("custom-model-1");
    }

    @Test
    @DisplayName("12. GEMINI_MODEL legacy single model selection works when GEMINI_MODELS is blank")
    void geminiModelLegacySingleModelWorks() {
        GeminiModelRouter router = new GeminiModelRouter(geminiClient, "");
        ReflectionTestUtils.setField(router, "legacyModel", "custom-legacy-model");

        List<String> models = router.getConfiguredModels();
        assertThat(models).containsExactly("custom-legacy-model");
        assertThat(router.getPrimaryModel()).isEqualTo("custom-legacy-model");
    }
}
