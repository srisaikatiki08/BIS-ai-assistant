package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bis_updates")
public class BISUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "publish_date", length = 50)
    private String date; // e.g. "10 March 2026", "2026-03-10"

    @Column(length = 100)
    private String category; // e.g. "Mandatory QCO", "CRS Amendment", "New Standard"

    @Column(name = "gazette_number", length = 150)
    private String gazetteNumber; // e.g. "S.O. 1246(E)"

    @Column(name = "effective_date", length = 100)
    private String effectiveDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "affected_standards", length = 300)
    private String affectedStandards;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    public BISUpdate() {
    }

    public BISUpdate(String title, String date, String category, String gazetteNumber,
                     String effectiveDate, String summary, String affectedStandards, String sourceUrl) {
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
