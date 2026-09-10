package com.bis.assistant.repository;

import com.bis.assistant.model.KnowledgeChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, Long> {

    List<KnowledgeChunk> findByDocumentIgnoreCase(String document);

    boolean existsByDocumentIgnoreCase(String document);

    @Transactional
    long deleteByDocumentIgnoreCase(String document);

    @Query("""
    SELECT k FROM KnowledgeChunk k
    WHERE LOWER(k.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(k.document) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(COALESCE(k.section, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(COALESCE(k.clause, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<KnowledgeChunk> searchByKeyword(@Param("keyword") String keyword);
}

