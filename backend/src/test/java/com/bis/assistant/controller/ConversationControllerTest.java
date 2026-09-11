package com.bis.assistant.controller;

import com.bis.assistant.dto.ConversationDTO;
import com.bis.assistant.dto.MessageDTO;
import com.bis.assistant.service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @Test
    @WithMockUser(username = "alpha@bis.gov.in", roles = {"USER"})
    void getAllConversations_Authenticated_ReturnsUserList() throws Exception {
        ConversationDTO conv = new ConversationDTO(1L, "sess-101", "Helmet Standards Inquiry", LocalDateTime.now(), LocalDateTime.now());
        when(conversationService.getAllConversations()).thenReturn(List.of(conv));

        mockMvc.perform(get("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sessionIdentifier").value("sess-101"))
                .andExpect(jsonPath("$[0].title").value("Helmet Standards Inquiry"));
    }

    @Test
    void getAllConversations_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alpha@bis.gov.in", roles = {"USER"})
    void getConversation_OwnedSession_ReturnsOkWithMessages() throws Exception {
        ConversationDTO conv = new ConversationDTO(1L, "sess-101", "Helmet Standards Inquiry", LocalDateTime.now(), LocalDateTime.now());
        conv.setMessages(List.of(
                new MessageDTO(1L, "user", "Tell me about IS 4151", LocalDateTime.now()),
                new MessageDTO(2L, "model", "IS 4151 specifies protective helmets for riders.", LocalDateTime.now())
        ));

        when(conversationService.getConversation("sess-101")).thenReturn(Optional.of(conv));

        mockMvc.perform(get("/api/conversations/sess-101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionIdentifier").value("sess-101"))
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.messages[0].role").value("user"))
                .andExpect(jsonPath("$.messages[1].role").value("model"));
    }

    @Test
    @WithMockUser(username = "alpha@bis.gov.in", roles = {"USER"})
    void getConversation_OtherUserOrNonExistentSession_ReturnsNotFound() throws Exception {
        when(conversationService.getConversation("sess-beta-private")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/conversations/sess-beta-private"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getConversation_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/conversations/sess-101"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alpha@bis.gov.in", roles = {"USER"})
    void deleteConversation_OwnedSession_ReturnsDeletedTrue() throws Exception {
        when(conversationService.deleteConversation("sess-101")).thenReturn(true);

        mockMvc.perform(delete("/api/conversations/sess-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.sessionIdentifier").value("sess-101"));
    }

    @Test
    @WithMockUser(username = "alpha@bis.gov.in", roles = {"USER"})
    void deleteConversation_OtherUserSession_ReturnsDeletedFalse() throws Exception {
        when(conversationService.deleteConversation("sess-beta-private")).thenReturn(false);

        mockMvc.perform(delete("/api/conversations/sess-beta-private"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.sessionIdentifier").value("sess-beta-private"));
    }

    @Test
    void deleteConversation_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/conversations/sess-101"))
                .andExpect(status().isUnauthorized());
    }
}
