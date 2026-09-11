package com.bis.assistant.service;

import com.bis.assistant.dto.KnowledgeUploadResponse;
import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TedPdfIngestionTest {

    @Autowired
    private PdfIngestionService pdfIngestionService;

    @Autowired
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Autowired
    private KnowledgeRetrievalService knowledgeRetrievalService;

    @Autowired
    private GeminiService geminiService;

    private byte[] pdfBytes;

    @BeforeEach
    void setUp() throws Exception {
        ClassPathResource resource = new ClassPathResource("standards/BIS_TED_Knowledge_Base.pdf");
        try (InputStream is = resource.getInputStream()) {
            pdfBytes = is.readAllBytes();
        }
    }

    @Test
    void testIngestTedPdfAndVerifyChunks() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "BIS_TED_Knowledge_Base.pdf",
                "application/pdf",
                pdfBytes
        );

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                file,
                "BIS Transport Engineering Department (TED)",
                "BIS TED Knowledge Base",
                "Transport Engineering",
                null,
                true
        );

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTotalPages()).isEqualTo(6);
        assertThat(response.getChunksCreated()).isGreaterThan(0);

        List<KnowledgeChunk> chunks = knowledgeChunkRepository.findByDocumentIgnoreCase("BIS Transport Engineering Department (TED)");
        assertThat(chunks).isNotEmpty();
        System.out.println("Total chunks created: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeChunk c = chunks.get(i);
            System.out.println(String.format("Chunk %d | Page %d | Section: %s | Clause: %s | Title: %s",
                    i + 1, c.getPageNumber(), c.getSection(), c.getClause(), c.getTitle()));
            System.out.println("Content preview: " + c.getContent().substring(0, Math.min(100, c.getContent().length())) + "...");
        }

        // Verify retrieval with TED-specific queries
        List<KnowledgeChunk> retrievedBraking = knowledgeRetrievalService.retrieveRelevantChunks("Which committee covers automotive braking and steering?");
        assertThat(retrievedBraking).isNotEmpty();
        assertThat(retrievedBraking.get(0).getContent()).containsIgnoringCase("TED 04");

        List<KnowledgeChunk> retrievedTyres = knowledgeRetrievalService.retrieveRelevantChunks("agricultural vehicle tyres standard IS 13154");
        assertThat(retrievedTyres).isNotEmpty();
        assertThat(retrievedTyres.get(0).getContent()).containsIgnoringCase("IS 13154:2015");

        // Verify citation generation without external URL
        String mockGeminiAnswer = "According to the Transport Engineering Department, TED 04 covers automotive braking systems [Source 1].";
        List<Map<String, Object>> citations = geminiService.extractCitations(mockGeminiAnswer, retrievedBraking);
        assertThat(citations).isNotEmpty();
        Map<String, Object> citation = citations.get(0);
        assertThat(citation.get("source")).isEqualTo("[Source 1]");
        assertThat(citation.get("document")).isEqualTo("BIS Transport Engineering Department (TED)");
        assertThat(citation.get("sourceUrl")).isNull();
        assertThat(citation.get("portalUrl")).isNull();
    }
}
