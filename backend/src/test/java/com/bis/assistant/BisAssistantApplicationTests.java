package com.bis.assistant;

import com.bis.assistant.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BisAssistantApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointReturnsStatus() {
        String url = "http://localhost:" + port + "/api/health";
        ResponseEntity<HealthResponse> response = restTemplate.getForEntity(url, HealthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("UP");
        assertThat(response.getBody().getActiveModel()).isEqualTo("gemini-2.5-flash");
        assertThat(response.getBody().getGeminiEndpoint()).isEqualTo("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
    }

    @Test
    void standardsEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/standards";
        ResponseEntity<StandardDTO[]> response = restTemplate.getForEntity(url, StandardDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void servicesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/services";
        ResponseEntity<BISServiceDTO[]> response = restTemplate.getForEntity(url, BISServiceDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void laboratoriesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/laboratories";
        ResponseEntity<LaboratoryDTO[]> response = restTemplate.getForEntity(url, LaboratoryDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updatesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/updates";
        ResponseEntity<BISUpdateDTO[]> response = restTemplate.getForEntity(url, BISUpdateDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void chatEndpointRejectsEmptyMessage() {
        String url = "http://localhost:" + port + "/api/chat";
        ChatRequest emptyRequest = new ChatRequest("", "en", null);
        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(url, emptyRequest, ChatResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void corsPreflightRequestAllowedForLocalhost5174() throws Exception {
        mockMvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5174")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
    }

    @Test
    void corsPreflightRequestAllowedForLocalhost5173() throws Exception {
        mockMvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
    }

    @Autowired
    private com.bis.assistant.service.KnowledgeRetrievalService knowledgeRetrievalService;

    @Test
    void knowledgeRetrievalFindsSeededChunks() {
        var chunks = knowledgeRetrievalService.retrieveRelevantChunks("What is the impact attenuation test for motorcycle helmets?");
        assertThat(chunks).isNotEmpty();
        assertThat(chunks.get(0).getDocument()).isEqualTo("IS 4151:2015");
        assertThat(chunks.get(0).getTitle()).isEqualTo("Impact Attenuation Requirements");

        String context = knowledgeRetrievalService.buildKnowledgeContext("What is the impact attenuation test for motorcycle helmets?");
        assertThat(context).contains("IS 4151:2015");
        assertThat(context).contains("300g");
    }

    @Test
    void uploadPdfAndRetrieveViaRAG() throws Exception {
        // Create an in-memory PDF using PDFBox
        byte[] pdfBytes;
        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new org.apache.pdfbox.pdmodel.font.PDType1Font(org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                cs.showText("Clause 8.2 Electric vehicles battery swapping safety limits and fire suppression.");
                cs.endText();
            }
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            doc.save(baos);
            pdfBytes = baos.toByteArray();
        }

        org.springframework.mock.web.MockMultipartFile pdfFile = new org.springframework.mock.web.MockMultipartFile(
                "file",
                "IS_17017.pdf",
                "application/pdf",
                pdfBytes
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/knowledge/upload")
                        .file(pdfFile)
                        .param("document", "IS 17017:2026")
                        .param("title", "EV Charging & Swapping Safety")
                        .param("sourceUrl", "https://standardsbis.bsbedge.com")
                        .param("overwrite", "true"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.document").value("IS 17017:2026"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.chunksCreated").isNumber());

        // Verify that the existing KnowledgeRetrievalService immediately finds the newly ingested chunks
        var chunks = knowledgeRetrievalService.retrieveRelevantChunks("What are the EV swapping safety limits?");
        assertThat(chunks).isNotEmpty();
        assertThat(chunks.stream().anyMatch(c -> "IS 17017:2026".equals(c.getDocument()))).isTrue();
    }

    @Test
    void corsActualRequestAllowedForLocalhost5174() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Origin", "http://localhost:5174"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"));
    }
}
