package com.bis.assistant.repository;

import com.bis.assistant.model.StandardDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardDocumentRepository extends JpaRepository<StandardDocument, Long> {
    List<StandardDocument> findByStandardId(Long standardId);
}
