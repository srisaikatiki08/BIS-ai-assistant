package com.bis.assistant.service;

import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DepartmentsBatchPdfIngestionTest {

    @Autowired
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Autowired
    private KnowledgeRetrievalService knowledgeRetrievalService;

    @Autowired
    private GeminiService geminiService;

    @Test
    void testAllDepartmentPdfsIngestedAndRetrievable() {
        List<String> departmentDocs = List.of(
                "BIS Transport Engineering Department (TED)",
                "BIS Civil Engineering Department (CED)",
                "BIS Chemical Department (CHD)",
                "BIS Medical Equipment Department (MHD)",
                "BIS Textiles Department (TXD)"
        );

        for (String docName : departmentDocs) {
            List<KnowledgeChunk> chunks = knowledgeChunkRepository.findByDocumentIgnoreCase(docName);
            assertThat(chunks)
                    .withFailMessage("Expected chunks for document: " + docName)
                    .isNotEmpty();
            System.out.println(String.format("✅ Document: '%s' -> %d chunks stored.", docName, chunks.size()));
        }

        // Test Civil Engineering Retrieval (CED)
        List<KnowledgeChunk> cedResults = knowledgeRetrievalService.retrieveRelevantChunks("civil engineering cement concrete standards CED");
        assertThat(cedResults).isNotEmpty();
        System.out.println("CED Query Result: " + cedResults.get(0).getTitle() + " | " + cedResults.get(0).getDocument());

        // Test Chemical Department Retrieval (CHD)
        List<KnowledgeChunk> chdResults = knowledgeRetrievalService.retrieveRelevantChunks("chemical department petrochemicals cosmetics standards CHD");
        assertThat(chdResults).isNotEmpty();
        System.out.println("CHD Query Result: " + chdResults.get(0).getTitle() + " | " + chdResults.get(0).getDocument());

        // Test Medical Equipment Retrieval (MHD)
        List<KnowledgeChunk> mhdResults = knowledgeRetrievalService.retrieveRelevantChunks("medical equipment hospital planning surgical instruments MHD");
        assertThat(mhdResults).isNotEmpty();
        System.out.println("MHD Query Result: " + mhdResults.get(0).getTitle() + " | " + mhdResults.get(0).getDocument());

        // Test Textiles Retrieval (TXD)
        List<KnowledgeChunk> txdResults = knowledgeRetrievalService.retrieveRelevantChunks("technical textiles geotextiles protective clothing TXD");
        assertThat(txdResults).isNotEmpty();
        System.out.println("TXD Query Result: " + txdResults.get(0).getTitle() + " | " + txdResults.get(0).getDocument());

        // Test Transport Engineering Retrieval (TED)
        List<KnowledgeChunk> tedResults = knowledgeRetrievalService.retrieveRelevantChunks("automotive braking roadworthiness TED 04");
        assertThat(tedResults).isNotEmpty();
        System.out.println("TED Query Result: " + tedResults.get(0).getTitle() + " | " + tedResults.get(0).getDocument());

        // Verify clean citation without fallback URL
        String mockGeminiResponse = "For civil engineering and structural safety, CED oversees building materials [Source 1].";
        List<Map<String, Object>> citations = geminiService.extractCitations(mockGeminiResponse, cedResults);
        assertThat(citations).isNotEmpty();
        Map<String, Object> citation = citations.get(0);
        assertThat(citation.get("source")).isEqualTo("[Source 1]");
        assertThat(citation.get("sourceUrl")).isNull();
        assertThat(citation.get("portalUrl")).isNull();
        System.out.println("Citation verified: " + citation);
    }
}
