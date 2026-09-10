package com.bis.assistant.service;

import com.bis.assistant.dto.ConversationDTO;
import com.bis.assistant.model.Conversation;
import com.bis.assistant.model.Message;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.ConversationRepository;
import com.bis.assistant.repository.MessageRepository;
import com.bis.assistant.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    private ConversationService conversationService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        conversationService = new ConversationService(conversationRepository, messageRepository, userRepository);

        userA = new User("User Alpha", "alpha@bis.gov.in", "passHashA");
        userA.setId(101L);

        userB = new User("User Beta", "beta@bis.gov.in", "passHashB");
        userB.setId(202L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContextUser(String email) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getOrCreateConversation_AuthenticatedUser_CreatesNewConversation() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-100")).thenReturn(Optional.empty());

        Conversation createdConv = new Conversation(userA, "sess-100", "ISI Standards Inquiry");
        createdConv.setId(1L);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(createdConv);

        Conversation result = conversationService.getOrCreateConversation("sess-100", "ISI Standards Inquiry");

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(userA);
        assertThat(result.getSessionIdentifier()).isEqualTo("sess-100");
        assertThat(result.getTitle()).isEqualTo("ISI Standards Inquiry");
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void getOrCreateConversation_ExistingConversation_ReturnsExisting() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));

        Conversation existing = new Conversation(userA, "sess-100", "Existing Title");
        existing.setId(1L);
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-100")).thenReturn(Optional.of(existing));

        Conversation result = conversationService.getOrCreateConversation("sess-100", "New Title");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Existing Title");
        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    void saveMessage_PersistsUserAndAssistantMessagesForAuthenticatedUser() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));

        Conversation conv = new Conversation(userA, "sess-100", "Initial Query");
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-100")).thenReturn(Optional.of(conv));

        Message savedMsg = new Message(conv, "user", "What is IS 4151?");
        savedMsg.setId(10L);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMsg);

        Message result = conversationService.saveMessage("sess-100", "user", "What is IS 4151?");

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo("user");
        assertThat(result.getContent()).isEqualTo("What is IS 4151?");
        assertThat(conv.getMessages()).hasSize(1);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void getAllConversations_ReturnsOnlyAuthenticatedUsersConversations() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));

        Conversation convA = new Conversation(userA, "sess-alpha-1", "Alpha Conversation");
        convA.setId(1L);
        when(conversationRepository.findAllByUserOrderByUpdatedAtDesc(userA)).thenReturn(List.of(convA));

        List<ConversationDTO> results = conversationService.getAllConversations();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSessionIdentifier()).isEqualTo("sess-alpha-1");
        assertThat(results.get(0).getTitle()).isEqualTo("Alpha Conversation");
        verify(conversationRepository).findAllByUserOrderByUpdatedAtDesc(userA);
    }

    @Test
    void getConversation_OwnerUser_ReturnsConversation() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));

        Conversation convA = new Conversation(userA, "sess-100", "Alpha Query");
        convA.setId(1L);
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-100")).thenReturn(Optional.of(convA));

        Optional<ConversationDTO> result = conversationService.getConversation("sess-100");

        assertThat(result).isPresent();
        assertThat(result.get().getSessionIdentifier()).isEqualTo("sess-100");
    }

    @Test
    void getConversation_DifferentUser_ReturnsEmpty() {
        // User A attempts to access session that belongs to User B
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));
        // Repository checks for (userA, "sess-beta-private") -> returns empty
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-beta-private")).thenReturn(Optional.empty());

        Optional<ConversationDTO> result = conversationService.getConversation("sess-beta-private");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteConversation_OwnerUser_DeletesAndReturnsTrue() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));

        Conversation convA = new Conversation(userA, "sess-100", "Alpha Query");
        convA.setId(1L);
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-100")).thenReturn(Optional.of(convA));

        boolean deleted = conversationService.deleteConversation("sess-100");

        assertThat(deleted).isTrue();
        verify(conversationRepository).delete(convA);
    }

    @Test
    void deleteConversation_DifferentUser_DoesNotDeleteAndReturnsFalse() {
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));
        when(conversationRepository.findByUserAndSessionIdentifier(userA, "sess-beta-private")).thenReturn(Optional.empty());

        boolean deleted = conversationService.deleteConversation("sess-beta-private");

        assertThat(deleted).isFalse();
        verify(conversationRepository, never()).delete(any());
    }

    @Test
    void twoDifferentUsers_CanUseSameSessionIdentifierWithoutConflict() {
        String sharedSessionId = "shared-session-123";

        // User A creation
        setSecurityContextUser("alpha@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("alpha@bis.gov.in")).thenReturn(Optional.of(userA));
        when(conversationRepository.findByUserAndSessionIdentifier(userA, sharedSessionId)).thenReturn(Optional.empty());
        Conversation convA = new Conversation(userA, sharedSessionId, "Alpha Topic");
        convA.setId(1L);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(convA);

        Conversation resultA = conversationService.getOrCreateConversation(sharedSessionId, "Alpha Topic");
        assertThat(resultA.getUser()).isEqualTo(userA);
        assertThat(resultA.getSessionIdentifier()).isEqualTo(sharedSessionId);

        // User B creation with the same session id
        setSecurityContextUser("beta@bis.gov.in");
        when(userRepository.findByEmailIgnoreCase("beta@bis.gov.in")).thenReturn(Optional.of(userB));
        when(conversationRepository.findByUserAndSessionIdentifier(userB, sharedSessionId)).thenReturn(Optional.empty());
        Conversation convB = new Conversation(userB, sharedSessionId, "Beta Topic");
        convB.setId(2L);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(convB);

        Conversation resultB = conversationService.getOrCreateConversation(sharedSessionId, "Beta Topic");
        assertThat(resultB.getUser()).isEqualTo(userB);
        assertThat(resultB.getSessionIdentifier()).isEqualTo(sharedSessionId);
        assertThat(resultB.getUser()).isNotEqualTo(resultA.getUser());
    }

    @Test
    void unauthenticatedAccess_ThrowsBadCredentialsException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> conversationService.getAllConversations())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Authentication required");

        assertThatThrownBy(() -> conversationService.getConversation("sess-100"))
                .isInstanceOf(BadCredentialsException.class);

        assertThatThrownBy(() -> conversationService.deleteConversation("sess-100"))
                .isInstanceOf(BadCredentialsException.class);

        assertThatThrownBy(() -> conversationService.saveMessage("sess-100", "user", "Hello"))
                .isInstanceOf(BadCredentialsException.class);
    }
}
