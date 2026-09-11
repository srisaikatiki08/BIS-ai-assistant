package com.bis.assistant.service;

import com.bis.assistant.dto.ConversationDTO;
import com.bis.assistant.dto.MessageDTO;
import com.bis.assistant.model.Conversation;
import com.bis.assistant.model.Message;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.ConversationRepository;
import com.bis.assistant.repository.MessageRepository;
import com.bis.assistant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Resolves the currently authenticated User entity from Spring Security's SecurityContext.
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadCredentialsException("Authentication required to access conversations.");
        }
        String email = authentication.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found for email: " + email));
    }

    /**
     * Retrieves or creates a conversation scoped to the authenticated user.
     */
    @Transactional
    public Conversation getOrCreateConversation(String sessionIdentifier, String defaultTitle) {
        User user = getAuthenticatedUser();
        return getOrCreateConversationForUser(user, sessionIdentifier, defaultTitle);
    }

    /**
     * Retrieves or creates a conversation for a specific User entity.
     */
    @Transactional
    public Conversation getOrCreateConversationForUser(User user, String sessionIdentifier, String defaultTitle) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when creating a conversation.");
        }

        String session = (sessionIdentifier != null && !sessionIdentifier.trim().isEmpty())
                ? sessionIdentifier.trim()
                : "sess-" + System.currentTimeMillis();

        return conversationRepository.findByUserAndSessionIdentifier(user, session).orElseGet(() -> {
            String title = (defaultTitle != null && !defaultTitle.trim().isEmpty()) ? defaultTitle : "New Inquiry";
            Conversation conv = new Conversation(user, session, title);
            logger.info("Created new conversation session '{}' for user id: {}", session, user.getId());
            return conversationRepository.save(conv);
        });
    }

    /**
     * Appends and persists a message in the conversation session of the authenticated user.
     */
    @Transactional
    public Message saveMessage(String sessionIdentifier, String role, String content) {
        User user = getAuthenticatedUser();
        return saveMessageForUser(user, sessionIdentifier, role, content);
    }

    /**
     * Appends and persists a message for a specific user.
     */
    @Transactional
    public Message saveMessageForUser(User user, String sessionIdentifier, String role, String content) {
        Conversation conversation = getOrCreateConversationForUser(user, sessionIdentifier, extractTitle(content));
        Message message = new Message(conversation, role, content);
        conversation.addMessage(message);
        return messageRepository.save(message);
    }

    /**
     * Returns all conversations belonging exclusively to the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> getAllConversations() {
        User user = getAuthenticatedUser();
        return conversationRepository.findAllByUserOrderByUpdatedAtDesc(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a specific conversation if it belongs to the authenticated user.
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getConversation(String sessionIdentifier) {
        User user = getAuthenticatedUser();
        return conversationRepository.findByUserAndSessionIdentifier(user, sessionIdentifier).map(this::mapToDTO);
    }

    /**
     * Deletes a conversation if it belongs to the authenticated user.
     */
    @Transactional
    public boolean deleteConversation(String sessionIdentifier) {
        User user = getAuthenticatedUser();
        Optional<Conversation> conv = conversationRepository.findByUserAndSessionIdentifier(user, sessionIdentifier);
        if (conv.isPresent()) {
            conversationRepository.delete(conv.get());
            logger.info("Deleted conversation session '{}' for user id: {}", sessionIdentifier, user.getId());
            return true;
        }
        return false;
    }

    private ConversationDTO mapToDTO(Conversation entity) {
        if (entity == null) return null;
        ConversationDTO dto = new ConversationDTO(
                entity.getId(),
                entity.getSessionIdentifier(),
                entity.getTitle(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        if (entity.getMessages() != null) {
            dto.setMessages(entity.getMessages().stream()
                    .map(m -> new MessageDTO(m.getId(), m.getRole(), m.getContent(), m.getTimestamp()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private String extractTitle(String content) {
        if (content == null || content.trim().isEmpty()) return "Product Advisory";
        String clean = content.trim();
        if (clean.length() > 30) {
            return clean.substring(0, 30) + "...";
        }
        return clean;
    }
}
