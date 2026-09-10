package com.bis.assistant.service;

import com.bis.assistant.dto.KnowledgeUploadResponse;
import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfIngestionServiceTest {

    @Mock
    private KnowledgeChunkRepository knowledgeChunkRepository;

    private PdfIngestionService pdfIngestionService;

    @BeforeEach
    void setUp() {
        pdfIngestionService = new PdfIngestionService(knowledgeChunkRepository);
    }

    private byte[] createSamplePdf(String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    void ingestPdfSuccessfulExtractionAndChunking() throws IOException {
        byte[] pdfBytes = createSamplePdf("Clause 4.1 Mechanical hazards in toys. All sharp edges must be protected.");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "IS_9873.pdf",
                "application/pdf",
                pdfBytes
        );

        when(knowledgeChunkRepository.existsByDocumentIgnoreCase("IS 9873:2019")).thenReturn(false);

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                file,
                "IS 9873:2019",
                "Safety of Toys Specification",
                "Mechanical Requirements",
                "https://manakonline.in/toys",
                true
        );

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getDocument()).isEqualTo("IS 9873:2019");
        assertThat(response.getChunksCreated()).isGreaterThanOrEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<KnowledgeChunk>> captor = ArgumentCaptor.forClass(List.class);
        verify(knowledgeChunkRepository).saveAll(captor.capture());

        List<KnowledgeChunk> savedChunks = captor.getValue();
        assertThat(savedChunks).isNotEmpty();
        KnowledgeChunk chunk = savedChunks.get(0);
        assertThat(chunk.getDocument()).isEqualTo("IS 9873:2019");
        assertThat(chunk.getTitle()).isEqualTo("Safety of Toys Specification");
        assertThat(chunk.getPageNumber()).isEqualTo(1);
        assertThat(chunk.getSourceUrl()).isEqualTo("https://manakonline.in/toys");
        assertThat(chunk.getContent()).contains("Mechanical hazards in toys");
        assertThat(chunk.getClause()).isEqualTo("Clause 4.1");
    }

    @Test
    void ingestPdfRejectsMissingOrEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                emptyFile, "IS 1000", null, null, null, true
        );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Missing or empty PDF file");
        verifyNoInteractions(knowledgeChunkRepository);
    }

    @Test
    void ingestPdfRejectsNonPdfFile() {
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "document.txt", "text/plain", "This is plain text".getBytes()
        );

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                textFile, "DOC-1", null, null, null, true
        );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Only PDF documents are supported");
        verifyNoInteractions(knowledgeChunkRepository);
    }

    @Test
    void ingestPdfRejectsDuplicateWhenOverwriteIsFalse() throws IOException {
        byte[] pdfBytes = createSamplePdf("IS 4151 helmet testing protocols.");
        MockMultipartFile file = new MockMultipartFile("file", "IS_4151.pdf", "application/pdf", pdfBytes);

        when(knowledgeChunkRepository.existsByDocumentIgnoreCase("IS 4151:2015")).thenReturn(true);

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                file, "IS 4151:2015", null, null, null, false
        );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("already exists");
        verify(knowledgeChunkRepository, never()).deleteByDocumentIgnoreCase(anyString());
        verify(knowledgeChunkRepository, never()).saveAll(anyList());
    }

    @Test
    void ingestPdfOverwritesDuplicateWhenOverwriteIsTrue() throws IOException {
        byte[] pdfBytes = createSamplePdf("IS 4151 helmet updated specifications.");
        MockMultipartFile file = new MockMultipartFile("file", "IS_4151.pdf", "application/pdf", pdfBytes);

        when(knowledgeChunkRepository.existsByDocumentIgnoreCase("IS 4151:2015")).thenReturn(true);
        when(knowledgeChunkRepository.deleteByDocumentIgnoreCase("IS 4151:2015")).thenReturn(3L);

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                file, "IS 4151:2015", null, null, null, true
        );

        assertThat(response.isSuccess()).isTrue();
        verify(knowledgeChunkRepository).deleteByDocumentIgnoreCase("IS 4151:2015");
        verify(knowledgeChunkRepository).saveAll(anyList());
    }

    @Test
    void splitIntoChunksSegmentsLongTextWithOverlap() {
        String text = "Paragraph 1 with detailed BIS testing standards. ".repeat(40);
        List<String> chunks = pdfIngestionService.splitIntoChunks(text, 500, 100);

        assertThat(chunks.size()).isGreaterThan(1);
        for (String chunk : chunks) {
            assertThat(chunk).isNotBlank();
        }
    }

    @Test
    void detectClauseIdentifiesStandardClauses() {
        assertThat(pdfIngestionService.detectClause("Requirements under Clause 5.4.1 for electrical safety"))
                .isEqualTo("Clause 5.4.1");
        assertThat(pdfIngestionService.detectClause("See Section 7 for helmet tests"))
                .isEqualTo("Section 7");
        assertThat(pdfIngestionService.detectClause("Regular text without markers")).isNull();
    }
}
