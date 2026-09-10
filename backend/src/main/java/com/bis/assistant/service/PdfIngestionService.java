package com.bis.assistant.service;

import com.bis.assistant.dto.KnowledgeUploadResponse;
import com.bis.assistant.model.KnowledgeChunk;
import com.bis.assistant.repository.KnowledgeChunkRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfIngestionService {

    private static final Logger logger = LoggerFactory.getLogger(PdfIngestionService.class);

    private static final int TARGET_CHUNK_SIZE = 800; // characters (~150-200 words)
    private static final int CHUNK_OVERLAP = 120;     // characters

    private final KnowledgeChunkRepository knowledgeChunkRepository;

    public PdfIngestionService(KnowledgeChunkRepository knowledgeChunkRepository) {
        this.knowledgeChunkRepository = knowledgeChunkRepository;
    }

    /**
     * Ingests a BIS PDF document, extracts page-by-page text, segments into KnowledgeChunks,
     * checks duplicate document records, and persists chunks into PostgreSQL.
     */
    @Transactional
    public KnowledgeUploadResponse ingestPdf(MultipartFile file,
                                            String documentName,
                                            String title,
                                            String section,
                                            String sourceUrl,
                                            boolean overwrite) {

        if (file == null || file.isEmpty()) {
            return KnowledgeUploadResponse.error(documentName, "Missing or empty PDF file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.toLowerCase().endsWith(".pdf")) {
            String contentType = file.getContentType();
            if (contentType != null && !contentType.equalsIgnoreCase("application/pdf") && !contentType.equalsIgnoreCase("application/octet-stream")) {
                return KnowledgeUploadResponse.error(documentName, "Only PDF documents are supported for knowledge ingestion.");
            }
        }

        String docIdentifier = (documentName != null && !documentName.trim().isEmpty())
                ? documentName.trim()
                : (originalFilename != null ? originalFilename.replaceFirst("(?i)\\.pdf$", "") : "BIS-DOCUMENT");

        // Duplicate Document Protection
        boolean exists = knowledgeChunkRepository.existsByDocumentIgnoreCase(docIdentifier);
        if (exists) {
            if (!overwrite) {
                logger.warn("Document '{}' already exists and overwrite is set to false.", docIdentifier);
                return KnowledgeUploadResponse.error(docIdentifier,
                        "Document '" + docIdentifier + "' already exists in the knowledge base. Set overwrite=true to update it.");
            }
            long deletedCount = knowledgeChunkRepository.deleteByDocumentIgnoreCase(docIdentifier);
            logger.info("Deleted {} existing chunks for document '{}' before re-ingestion.", deletedCount, docIdentifier);
        }

        List<KnowledgeChunk> chunksToSave = new ArrayList<>();
        int totalPages = 0;

        try (PDDocument pdDocument = Loader.loadPDF(file.getBytes())) {
            totalPages = pdDocument.getNumberOfPages();
            if (totalPages == 0) {
                return KnowledgeUploadResponse.error(docIdentifier, "Uploaded PDF has 0 pages.");
            }

            PDFTextStripper textStripper = new PDFTextStripper();
            int totalExtractedLength = 0;

            for (int page = 1; page <= totalPages; page++) {
                textStripper.setStartPage(page);
                textStripper.setEndPage(page);

                String pageText = textStripper.getText(pdDocument);
                if (pageText == null || pageText.trim().isEmpty()) {
                    continue;
                }

                totalExtractedLength += pageText.length();
                List<String> pageChunks = splitIntoChunks(pageText, TARGET_CHUNK_SIZE, CHUNK_OVERLAP);

                for (int i = 0; i < pageChunks.size(); i++) {
                    String chunkContent = pageChunks.get(i);
                    String detectedClause = detectClause(chunkContent);

                    String chunkTitle = title != null && !title.trim().isEmpty()
                            ? title.trim()
                            : docIdentifier + " (Page " + page + ")";

                    String chunkSection = section != null && !section.trim().isEmpty()
                            ? section.trim()
                            : (detectedClause != null ? detectedClause : "Section - Page " + page);

                    KnowledgeChunk chunk = new KnowledgeChunk(
                            docIdentifier,
                            chunkContent,
                            chunkTitle,
                            chunkSection,
                            detectedClause,
                            page,
                            (sourceUrl != null && !sourceUrl.trim().isEmpty()) ? sourceUrl.trim() : null
                    );

                    chunksToSave.add(chunk);
                }
            }

            if (totalExtractedLength == 0 || chunksToSave.isEmpty()) {
                return KnowledgeUploadResponse.error(docIdentifier, "PDF contains no extractable text or is a scanned image document.");
            }

            knowledgeChunkRepository.saveAll(chunksToSave);
            logger.info("Successfully ingested PDF '{}': {} pages, {} chunks created.", docIdentifier, totalPages, chunksToSave.size());

            return KnowledgeUploadResponse.success(
                    docIdentifier,
                    chunksToSave.size(),
                    totalPages,
                    "Document '" + docIdentifier + "' processed successfully with " + chunksToSave.size() + " chunks created."
            );

        } catch (IOException e) {
            logger.error("Failed to parse PDF document '{}': {}", docIdentifier, e.getMessage(), e);
            return KnowledgeUploadResponse.error(docIdentifier, "Failed to extract text from PDF: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during PDF ingestion for '{}': {}", docIdentifier, e.getMessage(), e);
            return KnowledgeUploadResponse.error(docIdentifier, "Failed to persist document knowledge: " + e.getMessage());
        }
    }

    /**
     * Splits text into overlapping segments with natural boundary awareness.
     */
    public List<String> splitIntoChunks(String text, int targetChunkSize, int overlapSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }

        String cleaned = text.replaceAll("\\r\\n", "\n").replaceAll("[ \\t]+", " ").trim();
        if (cleaned.length() <= targetChunkSize) {
            chunks.add(cleaned);
            return chunks;
        }

        int start = 0;
        while (start < cleaned.length()) {
            int end = Math.min(start + targetChunkSize, cleaned.length());

            if (end < cleaned.length()) {
                int boundary = -1;
                for (int i = end; i > Math.max(start + targetChunkSize / 2, end - 150); i--) {
                    char c = cleaned.charAt(i - 1);
                    if (c == '\n' || (c == '.' && (i == cleaned.length() || Character.isWhitespace(cleaned.charAt(i))))) {
                        boundary = i;
                        break;
                    }
                }
                if (boundary != -1) {
                    end = boundary;
                }
            }

            String chunk = cleaned.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= cleaned.length()) {
                break;
            }

            start = Math.max(start + 1, end - overlapSize);
        }

        return chunks;
    }

    /**
     * Detects standard clause/section markers within chunk content.
     */
    public String detectClause(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Pattern p = Pattern.compile("(?i)\\b(?:Clause|Section|Annex|Part)\\s+([0-9A-Za-z\\.\\-]+)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(0).trim();
        }

        Pattern numPattern = Pattern.compile("(?m)^\\s*([0-9]+\\.[0-9]+(?:\\.[0-9]+)*)\\b");
        Matcher numMatcher = numPattern.matcher(text);
        if (numMatcher.find()) {
            return numMatcher.group(1).trim();
        }

        return null;
    }
}
