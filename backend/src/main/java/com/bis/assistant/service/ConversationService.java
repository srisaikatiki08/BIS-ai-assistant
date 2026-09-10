package com.bis.assistant.service;

import com.bis.assistant.dto.ConversationDTO;
import com.bis.assistant.dto.MessageDTO;
import com.bis.assistant.model.Conversation;
import com.bis.assistant.model.Message;
import com.bis.assistant.repository.ConversationRepository;
import com.bis.assistant.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Conversation getOrCreateConversation(String sessionIdentifier, String defaultTitle) {
        String session = (sessionIdentifier != null && !sessionIdentifier.trim().isEmpty())
                ? sessionIdentifier.trim()
                : "sess-" + System.currentTimeMillis();

        return conversationRepository.findBySessionIdentifier(session).orElseGet(() -> {
            String title = (defaultTitle != null && !defaultTitle.trim().isEmpty()) ? defaultTitle : "New Inquiry";
            Conversation conv = new Conversation(session, title);
            return conversationRepository.save(conv);
        });
    }

    @Transactional
    public Message saveMessage(String sessionIdentifier, String role, String content) {
        Conversation conversation = getOrCreateConversation(sessionIdentifier, extractTitle(content));
        Message message = new Message(conversation, role, content);
        conversation.addMessage(message);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getAllConversations() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getConversation(String sessionIdentifier) {
        return conversationRepository.findBySessionIdentifier(sessionIdentifier).map(this::mapToDTO);
    }

    @Transactional
    public boolean deleteConversation(String sessionIdentifier) {
        Optional<Conversation> conv = conversationRepository.findBySessionIdentifier(sessionIdentifier);
        if (conv.isPresent()) {
            conversationRepository.delete(conv.get());
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
