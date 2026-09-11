package com.bis.assistant.service;

import com.bis.assistant.model.KnowledgeChunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GeminiServiceCitationTest {

    @Mock
    private BisKnowledgeService bisKnowledgeService;

    @Mock
    private KnowledgeRetrievalService knowledgeRetrievalService;

    private GeminiService geminiService;

    private KnowledgeChunk chunk1;
    private KnowledgeChunk chunk2;
    private KnowledgeChunk chunk3;

    @BeforeEach
    void setUp() {
        geminiService = new GeminiService(bisKnowledgeService, knowledgeRetrievalService);

        chunk1 = new KnowledgeChunk(
                "IS 4151:2015",
                "Motorcycle helmet peak acceleration <= 300g",
                "Helmet Impact Test",
                "Section 7",
                "7.1",
                8,
                "https://manakonline.in"
        );
        chunk1.setId(1L);

        chunk2 = new KnowledgeChunk(
                "IS 16046 (Part 2):2018",
                "Lithium cells thermal abuse test at 130°C for 10 min",
                "Thermal Abuse",
                "Clause 7.3",
                "7.3.2",
                15,
                "https://www.crsbis.in"
        );
        chunk2.setId(2L);

        chunk3 = new KnowledgeChunk(
                "IS/IEC 62368-1:2023",
                "Audio Video ICT safety energy source classification",
                "Energy Classification",
                "Clause 4",
                "4.1",
                12,
                "https://manakonline.in"
        );
        chunk3.setId(3L);
    }

    @Test
    void extractCitationsWithSingleReferenceReturnsCorrectMetadata() {
        String answer = "The thermal abuse test requires 130°C for 10 minutes. [Source 2]";
        List<KnowledgeChunk> chunks = List.of(chunk1, chunk2, chunk3);

        List<Map<String, Object>> citations = geminiService.extractCitations(answer, chunks);

        assertThat(citations).hasSize(1);
        Map<String, Object> citation = citations.get(0);
        assertThat(citation.get("source")).isEqualTo("[Source 2]");
        assertThat(citation.get("document")).isEqualTo("IS 16046 (Part 2):2018");
        assertThat(citation.get("clause")).isEqualTo("7.3.2");
        assertThat(citation.get("pageNumber")).isEqualTo(15);
        assertThat(citation.get("sourceUrl")).isEqualTo("https://www.crsbis.in");
    }

    @Test
    void extractCitationsWithMultipleReferencesReturnsAllValidCitations() {
        String answer = "Motorcycle helmets require shock attenuation [Source 1]. Lithium batteries must withstand thermal abuse [Source 2].";
        List<KnowledgeChunk> chunks = List.of(chunk1, chunk2, chunk3);

        List<Map<String, Object>> citations = geminiService.extractCitations(answer, chunks);

        assertThat(citations).hasSize(2);
        assertThat(citations.get(0).get("source")).isEqualTo("[Source 1]");
        assertThat(citations.get(0).get("document")).isEqualTo("IS 4151:2015");
        assertThat(citations.get(1).get("source")).isEqualTo("[Source 2]");
        assertThat(citations.get(1).get("document")).isEqualTo("IS 16046 (Part 2):2018");
    }

    @Test
    void extractCitationsIgnoresOutOfBoundsReferences() {
        String answer = "This statement references an invalid source [Source 99] and a valid one [Source 1].";
        List<KnowledgeChunk> chunks = List.of(chunk1, chunk2);

        List<Map<String, Object>> citations = geminiService.extractCitations(answer, chunks);

        assertThat(citations).hasSize(1);
        assertThat(citations.get(0).get("source")).isEqualTo("[Source 1]");
        assertThat(citations.get(0).get("document")).isEqualTo("IS 4151:2015");
    }

    @Test
    void extractCitationsDeduplicatesRepeatedReferences() {
        String answer = "First requirement under [Source 1]. Second requirement also under [Source 1].";
        List<KnowledgeChunk> chunks = List.of(chunk1, chunk2);

        List<Map<String, Object>> citations = geminiService.extractCitations(answer, chunks);

        assertThat(citations).hasSize(1);
        assertThat(citations.get(0).get("source")).isEqualTo("[Source 1]");
    }

    @Test
    void extractCitationsWithNoReferencesReturnsEmptyList() {
        String answer = "General guidance without any citation tags.";
        List<KnowledgeChunk> chunks = List.of(chunk1, chunk2);

        List<Map<String, Object>> citations = geminiService.extractCitations(answer, chunks);

        assertThat(citations).isEmpty();
    }

    @Test
    void extractCitationsWithoutSourceUrlLeavesUrlAbsent() {
        KnowledgeChunk chunkWithoutUrl = new KnowledgeChunk(
                "IS 10500:2012",
                "Drinking water specifications",
                "Water Quality",
                "Section 3",
                "3.1",
                4,
                null
        );
        chunkWithoutUrl.setId(4L);

        String answer = "Drinking water standard parameters [Source 1].";
        List<Map<String, Object>> citations = geminiService.extractCitations(answer, List.of(chunkWithoutUrl));

        assertThat(citations).hasSize(1);
        Map<String, Object> citation = citations.get(0);
        assertThat(citation.get("source")).isEqualTo("[Source 1]");
        assertThat(citation.get("document")).isEqualTo("IS 10500:2012");
        assertThat(citation.get("sourceUrl")).isNull();
        assertThat(citation.get("portalUrl")).isNull();
    }
}
