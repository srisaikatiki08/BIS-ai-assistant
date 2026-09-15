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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeminiModelFallbackTest {

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
        geminiModelRouter = new GeminiModelRouter(geminiClient, "gemini-3-flash-preview,gemini-2.5-flash,gemini-2.5-pro");
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
    @DisplayName("A. First model succeeds -> Returns first model response immediately")
    void firstModelSucceedsImmediately() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("According to IS 4151:2015 [Source 1], helmet impact must not exceed 300g.", "gemini-3-flash-preview"));

        ChatRequest request = new ChatRequest("What is IS 4151 helmet acceleration?", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-3-flash-preview");
        assertThat(response.getAnswer()).contains("300g");
        assertThat(response.getCitations()).hasSize(1);
        assertThat(response.getCitations().get(0).get("document")).isEqualTo("IS 4151:2015");

        // Verify fallback models were NOT attempted
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("B. First model returns HTTP 503 (Overloaded) -> Second model is attempted and succeeds")
    void firstModelFails503FallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 503, "The model is currently experiencing high demand.", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("IS 4151:2015 [Source 1] specifies motorcycle protective helmets.", "gemini-2.5-flash"));

        ChatRequest request = new ChatRequest("Explain IS 4151", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-2.5-flash");
        assertThat(response.getAnswer()).contains("protective helmets");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("C. First model returns HTTP 429 (Rate Limit / Quota) -> Second model is attempted and succeeds")
    void firstModelFails429FallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 429, "Resource has been exhausted (e.g. check quota).", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Under IS 4151 [Source 1], tests must verify shell integrity.", "gemini-2.5-flash"));

        ChatRequest request = new ChatRequest("Explain helmet requirements", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-2.5-flash");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("D. First model times out -> Fallback model is attempted and succeeds")
    void firstModelTimesOutFallbackSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 0, "Connection timeout reaching Gemini endpoint", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Fallback answer verified with IS 4151 [Source 1].", "gemini-2.5-flash"));

        ChatRequest request = new ChatRequest("Helmet impact test query", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-2.5-flash");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("E. First two models fail with retryable errors, third succeeds")
    void firstTwoFailThirdSucceeds() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-2.5-flash", 429, "Rate limited", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Third model response for IS 4151 [Source 1].", "gemini-2.5-pro"));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getModel()).contains("gemini-2.5-pro");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("F. All models fail -> Clean controlled error message returned to user")
    void allModelsFailReturnsCleanError() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-2.5-flash", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-2.5-pro", 429, "Quota exhausted", true, false));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).contains("All configured AI models are temporarily unavailable");

        // Confirm each model was attempted exactly once
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("G. Invalid API Key (HTTP 401/403) -> Fail-fast immediately, does not waste attempts on other models")
    void invalidApiKeyFailsFastWithoutTryingOtherModels() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 401, "API_KEY_INVALID", false, true));

        ChatRequest request = new ChatRequest("Helmet testing", "en", null);
        ChatResponse response = geminiService.generateBisAnswer(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).contains("API_KEY_INVALID");

        verify(geminiClient, times(1)).callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString());
        verify(geminiClient, never()).callGenerateContent(eq("gemini-2.5-pro"), anyMap(), anyString(), anyString());
    }

    @Test
    @DisplayName("H. RAG context is retrieved once and the identical payload is reused across fallback attempts")
    void ragContextConstructedOnceAndReused() {
        when(geminiClient.callGenerateContent(eq("gemini-3-flash-preview"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.failure("gemini-3-flash-preview", 503, "High demand", true, false));

        when(geminiClient.callGenerateContent(eq("gemini-2.5-flash"), anyMap(), anyString(), anyString()))
                .thenReturn(GeminiModelResult.success("Answer from fallback model [Source 1].", "gemini-2.5-flash"));

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
    @DisplayName("I. Model router parses comma-separated list, trims, removes 'models/' prefix, and deduplicates")
    void modelRouterParsesAndDeduplicatesList() {
        GeminiModelRouter router = new GeminiModelRouter(geminiClient, " models/gemini-3-flash-preview, gemini-2.5-flash , gemini-3-flash-preview , gemini-2.5-pro ");
        List<String> models = router.getConfiguredModels();

        assertThat(models).containsExactly("gemini-3-flash-preview", "gemini-2.5-flash", "gemini-2.5-pro");
        assertThat(router.getPrimaryModel()).isEqualTo("gemini-3-flash-preview");
    }

    @Test
    @DisplayName("J. Model router falls back to legacy single model if GEMINI_MODELS is blank")
    void modelRouterFallsBackToLegacySingleModel() {
        GeminiModelRouter router = new GeminiModelRouter(geminiClient, "");
        ReflectionTestUtils.setField(router, "legacyModel", "gemini-2.5-flash");

        List<String> models = router.getConfiguredModels();
        assertThat(models).containsExactly("gemini-2.5-flash");
        assertThat(router.getPrimaryModel()).isEqualTo("gemini-2.5-flash");
    }
}
