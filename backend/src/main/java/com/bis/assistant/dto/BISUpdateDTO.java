package com.bis.assistant.dto;

public class BISUpdateDTO {
    private Long id;
    private String title;
    private String date;
    private String category;
    private String gazetteNumber;
    private String effectiveDate;
    private String summary;
    private String affectedStandards;
    private String sourceUrl;

    public BISUpdateDTO() {
    }

    public BISUpdateDTO(Long id, String title, String date, String category, String gazetteNumber,
                        String effectiveDate, String summary, String affectedStandards, String sourceUrl) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.category = category;
        this.gazetteNumber = gazetteNumber;
        this.effectiveDate = effectiveDate;
        this.summary = summary;
        this.affectedStandards = affectedStandards;
        this.sourceUrl = sourceUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGazetteNumber() {
        return gazetteNumber;
    }

    public void setGazetteNumber(String gazetteNumber) {
        this.gazetteNumber = gazetteNumber;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAffectedStandards() {
        return affectedStandards;
    }

    public void setAffectedStandards(String affectedStandards) {
        this.affectedStandards = affectedStandards;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
