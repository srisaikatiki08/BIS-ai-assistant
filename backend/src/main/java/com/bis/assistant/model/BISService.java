package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bis_services")
public class BISService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_code", unique = true, nullable = false, length = 100)
    private String serviceCode;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(length = 150)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_beneficiary", length = 250)
    private String targetBeneficiary;

    @Column(name = "key_features", columnDefinition = "TEXT")
    private String keyFeatures;

    @Column(name = "portal_url", length = 500)
    private String portalUrl;

    public BISService() {
    }

    public BISService(String serviceCode, String title, String category, String description,
                      String targetBeneficiary, String keyFeatures, String portalUrl) {
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
