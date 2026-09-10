package com.bis.assistant.dto;

public class BISServiceDTO {
    private Long id;
    private String serviceCode;
    private String title;
    private String category;
    private String description;
    private String targetBeneficiary;
    private String keyFeatures;
    private String portalUrl;

    public BISServiceDTO() {
    }

    public BISServiceDTO(Long id, String serviceCode, String title, String category,
                         String description, String targetBeneficiary, String keyFeatures, String portalUrl) {
        this.id = id;
        this.serviceCode = serviceCode;
        this.title = title;
        this.category = category;
        this.description = description;
        this.targetBeneficiary = targetBeneficiary;
        this.keyFeatures = keyFeatures;
        this.portalUrl = portalUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetBeneficiary() {
        return targetBeneficiary;
    }

    public void setTargetBeneficiary(String targetBeneficiary) {
        this.targetBeneficiary = targetBeneficiary;
    }

    public String getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(String keyFeatures) {
        this.keyFeatures = keyFeatures;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }
}
