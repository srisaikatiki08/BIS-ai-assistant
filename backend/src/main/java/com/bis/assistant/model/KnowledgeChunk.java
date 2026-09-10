package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String document; // e.g. "IS/IEC 62368-1:2023", "MeitY CRS Notification"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 300)
    private String title;

    @Column(length = 150)
    private String section;

    @Column(length = 100)
    private String clause;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    public KnowledgeChunk() {
    }

    public KnowledgeChunk(String document, String content, String title, String section,
                          String clause, Integer pageNumber, String sourceUrl) {
        this.document = document;
        this.content = content;
        this.title = title;
        this.section = section;
        this.clause = clause;
        this.pageNumber = pageNumber;
        this.sourceUrl = sourceUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
