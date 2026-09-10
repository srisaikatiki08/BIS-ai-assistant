package com.bis.assistant.repository;

import com.bis.assistant.model.Conversation;
import com.bis.assistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUserAndSessionIdentifier(User user, String sessionIdentifier);

    List<Conversation> findAllByUserOrderByUpdatedAtDesc(User user);

    boolean existsByUserAndSessionIdentifier(User user, String sessionIdentifier);

    long deleteByUserAndSessionIdentifier(User user, String sessionIdentifier);
}
