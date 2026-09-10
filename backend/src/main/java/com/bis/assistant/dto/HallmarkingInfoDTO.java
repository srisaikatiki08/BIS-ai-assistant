package com.bis.assistant.dto;

public class HallmarkingInfoDTO {
    private Long id;
    private String title;
    private String standardReference;
    private Integer huidDigits;
    private String description;
    private String applicableMetals;
    private String registrationProcess;
    private String consumerVerificationSteps;

    public HallmarkingInfoDTO() {
    }

    public HallmarkingInfoDTO(Long id, String title, String standardReference, Integer huidDigits,
                              String description, String applicableMetals, String registrationProcess,
                              String consumerVerificationSteps) {
        this.id = id;
        this.title = title;
        this.standardReference = standardReference;
        this.huidDigits = huidDigits;
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
