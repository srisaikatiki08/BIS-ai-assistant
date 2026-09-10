package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hallmarking_information")
public class HallmarkingInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 100)
    private String standardReference; // e.g. "IS 1417 : 2016", "IS 2112 : 2014"

    @Column(name = "huid_digits")
    private Integer huidDigits = 6;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "applicable_metals", length = 150)
    private String applicableMetals; // e.g. "Gold, Silver"

    @Column(name = "registration_process", columnDefinition = "TEXT")
    private String registrationProcess;

    @Column(name = "consumer_verification_steps", columnDefinition = "TEXT")
    private String consumerVerificationSteps;

    public HallmarkingInformation() {
    }

    public HallmarkingInformation(String title, String standardReference, Integer huidDigits,
                                  String description, String applicableMetals,
                                  String registrationProcess, String consumerVerificationSteps) {
        this.title = title;
        this.standardReference = standardReference;
        this.huidDigits = huidDigits != null ? huidDigits : 6;
        this.description = description;
        this.applicableMetals = applicableMetals;
        this.registrationProcess = registrationProcess;
        this.consumerVerificationSteps = consumerVerificationSteps;
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

    public String getStandardReference() {
        return standardReference;
    }

    public void setStandardReference(String standardReference) {
        this.standardReference = standardReference;
    }

    public Integer getHuidDigits() {
        return huidDigits;
    }

    public void setHuidDigits(Integer huidDigits) {
        this.huidDigits = huidDigits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplicableMetals() {
        return applicableMetals;
    }

    public void setApplicableMetals(String applicableMetals) {
        this.applicableMetals = applicableMetals;
    }

    public String getRegistrationProcess() {
        return registrationProcess;
    }

    public void setRegistrationProcess(String registrationProcess) {
        this.registrationProcess = registrationProcess;
    }

    public String getConsumerVerificationSteps() {
        return consumerVerificationSteps;
    }

    public void setConsumerVerificationSteps(String consumerVerificationSteps) {
        this.consumerVerificationSteps = consumerVerificationSteps;
    }
}
