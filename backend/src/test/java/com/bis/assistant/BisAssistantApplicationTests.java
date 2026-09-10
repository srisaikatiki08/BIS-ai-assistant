package com.bis.assistant;

import com.bis.assistant.dto.*;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.UserRepository;
import com.bis.assistant.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testAuthToken;
    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        User testUser = userRepository.findByEmailIgnoreCase("admin@bis.gov.in")
                .orElseGet(() -> userRepository.save(new User("Admin User", "admin@bis.gov.in", passwordEncoder.encode("Password@123"))));

        testAuthToken = jwtTokenProvider.generateToken(testUser.getEmail(), testUser.getId());
        authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(testAuthToken);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

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
    }

    @Test
    void standardsEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/standards";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<StandardDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, StandardDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void servicesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/services";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<BISServiceDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, BISServiceDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void laboratoriesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/laboratories";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<LaboratoryDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, LaboratoryDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updatesEndpointReturnsRecords() {
        String url = "http://localhost:" + port + "/api/updates";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<BISUpdateDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, BISUpdateDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void chatEndpointRejectsEmptyMessage() {
        String url = "http://localhost:" + port + "/api/chat";
        ChatRequest emptyRequest = new ChatRequest("", "en", null);
        HttpEntity<ChatRequest> entity = new HttpEntity<>(emptyRequest, authHeaders);
        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(url, entity, ChatResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void unauthenticatedAccessToProtectedEndpointFails() {
        String url = "http://localhost:" + port + "/api/standards";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void corsPreflightRequestAllowedForLocalhost5174() throws Exception {
        mockMvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5174")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type, Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
    }

    @Test
    void corsPreflightRequestAllowedForLocalhost5173() throws Exception {
        mockMvc.perform(options("/api/chat")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type, Authorization"))
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
                        .header("Authorization", "Bearer " + testAuthToken)
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
