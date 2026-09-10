package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "standard_documents")
public class StandardDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @Column(name = "clause_number", length = 100)
    private String clauseNumber;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    public StandardDocument() {
    }

    public StandardDocument(String clauseNumber, String title, String description, String sourceUrl) {
        this.clauseNumber = clauseNumber;
        this.title = title;
        this.description = description;
        this.sourceUrl = sourceUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Standard getStandard() {
        return standard;
    }

    public void setStandard(Standard standard) {
        this.standard = standard;
    }

    public String getClauseNumber() {
        return clauseNumber;
    }

    public void setClauseNumber(String clauseNumber) {
        this.clauseNumber = clauseNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
