package com.bis.assistant.dto;

public class StandardDocumentDTO {
    private Long id;
    private String clauseNumber;
    private String title;
    private String description;
    private String sourceUrl;

    public StandardDocumentDTO() {
    }

    public StandardDocumentDTO(Long id, String clauseNumber, String title, String description, String sourceUrl) {
        this.id = id;
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
