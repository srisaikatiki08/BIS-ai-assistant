package com.bis.assistant.controller;

import com.bis.assistant.dto.KnowledgeUploadResponse;
import com.bis.assistant.service.PdfIngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = {
        "https://bis-ai-assistant-nine.vercel.app",
        "http://localhost:5173", "http://127.0.0.1:5173",
        "http://localhost:5174", "http://127.0.0.1:5174",
        "http://localhost:5175", "http://127.0.0.1:5175",
        "http://localhost:3000", "http://127.0.0.1:3000"
}, allowCredentials = "true")
public class KnowledgeController {

    private final PdfIngestionService pdfIngestionService;

    public KnowledgeController(PdfIngestionService pdfIngestionService) {
        this.pdfIngestionService = pdfIngestionService;
    }

    /**
     * POST /api/knowledge/upload
     * Ingests a BIS standard or regulatory PDF document, splits content into RAG knowledge chunks,
     * and persists records to PostgreSQL for retrieval by Gemini AI chat.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KnowledgeUploadResponse> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "document", required = false) String document,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "section", required = false) String section,
            @RequestParam(value = "sourceUrl", required = false) String sourceUrl,
            @RequestParam(value = "overwrite", required = false, defaultValue = "true") boolean overwrite
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    KnowledgeUploadResponse.error(document != null ? document : "UNKNOWN", "Please select a non-empty PDF file to upload.")
            );
        }

        KnowledgeUploadResponse response = pdfIngestionService.ingestPdf(
                file, document, title, section, sourceUrl, overwrite
        );

        if (!response.isSuccess()) {
            if (response.getMessage() != null && response.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
