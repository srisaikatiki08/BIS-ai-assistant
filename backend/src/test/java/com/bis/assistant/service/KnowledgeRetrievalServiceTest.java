package com.bis.assistant.service;

import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KnowledgeRetrievalServiceTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    private KnowledgeRetrievalService retrievalService;

    @BeforeEach
    void setUp() {
        when(knowledgeChunkRepository.searchByKeyword(anyString())).thenReturn(Collections.emptyList());
        retrievalService = new KnowledgeRetrievalService(knowledgeChunkRepository);
    }

    @Test
    void retrieveRelevantChunksWithNullOrEmptyQueryReturnsEmptyList() {
        assertThat(retrievalService.retrieveRelevantChunks(null)).isEmpty();
        assertThat(retrievalService.retrieveRelevantChunks("")).isEmpty();
        assertThat(retrievalService.retrieveRelevantChunks("   ")).isEmpty();
    }

    @Test
    void retrieveRelevantChunksWithOnlyStopWordsReturnsEmptyList() {
        assertThat(retrievalService.retrieveRelevantChunks("what is the a an for to of in")).isEmpty();
    }

    @Test
    void ranksIs16046AboveUnrelatedChunksForIs16046Query() {
        KnowledgeChunk helmetChunk = new KnowledgeChunk(
                "IS 4151:2015",
                "Motorcycle helmet requirements and peak acceleration.",
                "Impact Attenuation Requirements",
                "Section 7",
                "7.1",
                8,
                "https://manakonline.in"
        );
        helmetChunk.setId(1L);

        KnowledgeChunk lithiumChunk = new KnowledgeChunk(
                "IS 16046 (Part 2):2018",
                "Lithium secondary cells safety requirements, thermal abuse and short circuit testing.",
                "Lithium Cell Safety",
                "Clause 7",
                "7.3",
                15,
                "https://www.crsbis.in"
        );
        lithiumChunk.setId(2L);

        when(knowledgeChunkRepository.searchByKeyword("16046")).thenReturn(List.of(lithiumChunk));
        when(knowledgeChunkRepository.searchByKeyword("lithium")).thenReturn(List.of(lithiumChunk));
        when(knowledgeChunkRepository.searchByKeyword("safety")).thenReturn(List.of(helmetChunk, lithiumChunk));
        when(knowledgeChunkRepository.searchByKeyword("requirements")).thenReturn(List.of(helmetChunk, lithiumChunk));

        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("safety requirements for IS 16046 lithium batteries");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getId()).isEqualTo(2L);
        assertThat(results.get(0).getDocument()).isEqualTo("IS 16046 (Part 2):2018");
    }

    @Test
    void ranksChunksWithMultipleMatchingTermsAboveSingleTermChunks() {
        KnowledgeChunk singleTermChunk = new KnowledgeChunk(
                "IS 16046:2018",
                "General battery storage guidelines and operating temperatures.",
                "Storage Guidelines",
                "Section 3",
                "3.1",
                5,
                "https://www.crsbis.in"
        );
        singleTermChunk.setId(1L);

        KnowledgeChunk multiTermChunk = new KnowledgeChunk(
                "IS 16046:2018",
                "Mechanical crush test requires applying 13 kN force between flat surfaces without explosion.",
                "Mechanical Crush Test",
                "Clause 7.3",
                "7.3.5",
                18,
                "https://www.crsbis.in"
        );
        multiTermChunk.setId(2L);

        when(knowledgeChunkRepository.searchByKeyword("crush")).thenReturn(List.of(multiTermChunk));
        when(knowledgeChunkRepository.searchByKeyword("test")).thenReturn(List.of(multiTermChunk));
        when(knowledgeChunkRepository.searchByKeyword("battery")).thenReturn(List.of(singleTermChunk, multiTermChunk));

        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("crush test for battery");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getId()).isEqualTo(2L);
        assertThat(results.get(0).getTitle()).isEqualTo("Mechanical Crush Test");
    }

    @Test
    void exactStandardIdentifierMatchingReceivesHigherPriority() {
        KnowledgeChunk helmetChunk = new KnowledgeChunk(
                "IS 4151:2015",
                "Protective helmet specifications and impact testing.",
                "Helmet Safety",
                "Clause 4",
                "4.1",
                4,
                "https://manakonline.in"
        );
        helmetChunk.setId(1L);

        KnowledgeChunk waterChunk = new KnowledgeChunk(
                "IS 10500:2012",
                "Drinking water specifications, TDS, turbidity and coliform testing.",
                "Water Specification",
                "Section 2",
                "2.1",
                2,
                "https://manakonline.in"
        );
        waterChunk.setId(2L);

        when(knowledgeChunkRepository.searchByKeyword("4151")).thenReturn(List.of(helmetChunk));
        when(knowledgeChunkRepository.searchByKeyword("testing")).thenReturn(List.of(waterChunk, helmetChunk));

        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("testing under IS 4151:2015");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getDocument()).isEqualTo("IS 4151:2015");
    }

    @Test
    void retrieveRelevantChunksDeduplicatesChunks() {
        KnowledgeChunk chunk1 = new KnowledgeChunk(
                "IS 4151:2015",
                "Motorcycle helmet peak acceleration <= 300g",
                "Impact Attenuation",
                "Section 7",
                "7.1",
                8,
                "https://manakonline.in"
        );
        chunk1.setId(1L);

        when(knowledgeChunkRepository.searchByKeyword("motorcycle")).thenReturn(List.of(chunk1));
        when(knowledgeChunkRepository.searchByKeyword("helmet")).thenReturn(List.of(chunk1));

        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("motorcycle helmet");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void retrieveRelevantChunksLimitsToFiveChunksInRankedOrder() {
        KnowledgeChunk c1 = new KnowledgeChunk("IS 1", "Standard Content 1", "T1", "S1", "C1", 1, "url1"); c1.setId(1L);
        KnowledgeChunk c2 = new KnowledgeChunk("IS 2", "Standard Content 2", "T2", "S2", "C2", 2, "url2"); c2.setId(2L);
        KnowledgeChunk c3 = new KnowledgeChunk("IS 3", "Standard Content 3", "T3", "S3", "C3", 3, "url3"); c3.setId(3L);
        KnowledgeChunk c4 = new KnowledgeChunk("IS 4", "Standard Content 4", "T4", "S4", "C4", 4, "url4"); c4.setId(4L);
        KnowledgeChunk c5 = new KnowledgeChunk("IS 5", "Standard Content 5", "T5", "S5", "C5", 5, "url5"); c5.setId(5L);
        KnowledgeChunk c6 = new KnowledgeChunk("IS 6", "Standard Content 6", "T6", "S6", "C6", 6, "url6"); c6.setId(6L);

        when(knowledgeChunkRepository.searchByKeyword("standard"))
                .thenReturn(List.of(c1, c2, c3, c4, c5, c6));

        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("standard");

        assertThat(results).hasSize(5);
    }

    @Test
    void unrelatedQueryReturnsEmptyList() {
        List<KnowledgeChunk> results = retrievalService.retrieveRelevantChunks("interstellar spacecraft");
        assertThat(results).isEmpty();
    }

    @Test
    void buildKnowledgeContextWithChunksFormatsCorrectly() {
        KnowledgeChunk chunk = new KnowledgeChunk(
                "IS 4151:2015",
                "Motorcycle protective helmet impact attenuation requires peak acceleration to remain below 300g.",
                "Impact Attenuation Requirements",
                "Section 7",
                "7.1",
                8,
                "https://manakonline.in"
        );
        chunk.setId(1L);

        String context = retrievalService.buildKnowledgeContext(List.of(chunk));

        assertThat(context).contains("[Source 1]");
        assertThat(context).contains("Document: IS 4151:2015");
        assertThat(context).contains("Title: Impact Attenuation Requirements");
        assertThat(context).contains("Section: Section 7");
        assertThat(context).contains("Clause: 7.1");
        assertThat(context).contains("Page: 8");
        assertThat(context).contains("Content: Motorcycle protective helmet impact attenuation requires peak acceleration to remain below 300g.");
        assertThat(context).contains("Source URL: https://manakonline.in");
    }

    @Test
    void buildKnowledgeContextWithMultipleChunksPreservesSourceOrder() {
        KnowledgeChunk chunk1 = new KnowledgeChunk("IS 4151:2015", "Content 1", "Title 1", "Section 1", "1.1", 1, "url1");
        chunk1.setId(1L);
        KnowledgeChunk chunk2 = new KnowledgeChunk("IS 16046:2018", "Content 2", "Title 2", "Section 2", "2.1", 2, "url2");
        chunk2.setId(2L);

        String context = retrievalService.buildKnowledgeContext(List.of(chunk1, chunk2));

        assertThat(context).contains("[Source 1]");
        assertThat(context).contains("Document: IS 4151:2015");
        assertThat(context).contains("[Source 2]");
        assertThat(context).contains("Document: IS 16046:2018");
        assertThat(context.indexOf("[Source 1]")).isLessThan(context.indexOf("[Source 2]"));
    }

    @Test
    void buildKnowledgeContextWhenNoChunksFoundReturnsFallback() {
        String context = retrievalService.buildKnowledgeContext(List.of());
        assertThat(context).isEqualTo("No directly matching BIS knowledge chunks were found in the local knowledge base.");

        String contextFromNull = retrievalService.buildKnowledgeContext((List<KnowledgeChunk>) null);
        assertThat(contextFromNull).isEqualTo("No directly matching BIS knowledge chunks were found in the local knowledge base.");
    }

    @Test
    void extractKeywordsFiltersStopWordsAndShortTokens() {
        List<String> keywords = retrievalService.extractKeywords("What is the impact attenuation for IS 4151:2015?");
        assertThat(keywords).contains("impact", "attenuation", "4151", "2015");
        assertThat(keywords).doesNotContain("what", "is", "the", "for");
    }
}
