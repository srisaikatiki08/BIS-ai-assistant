package com.bis.assistant.dto;

import java.util.ArrayList;
import java.util.List;

public class StandardDTO {
    private Long id;
    private String standardCode;
    private String isNumber;
    private String title;
    private String division;
    private String category;
    private String year;
    private String status;
    private Boolean isMandatory;
    private String scheme;
    private String qcoOrder;
    private String scope;
    private String licensingProcess;
    private String feeCategory;
    private List<StandardDocumentDTO> documents = new ArrayList<>();
    private List<TestingRequirementDTO> testingRequirements = new ArrayList<>();
    private List<String> applicableProducts = new ArrayList<>();

    public StandardDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStandardCode() {
        return standardCode;
    }

    public void setStandardCode(String standardCode) {
        this.standardCode = standardCode;
    }

    public String getIsNumber() {
        return isNumber;
    }

    public void setIsNumber(String isNumber) {
        this.isNumber = isNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getQcoOrder() {
        return qcoOrder;
    }

    public void setQcoOrder(String qcoOrder) {
        this.qcoOrder = qcoOrder;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLicensingProcess() {
        return licensingProcess;
    }

    public void setLicensingProcess(String licensingProcess) {
        this.licensingProcess = licensingProcess;
    }

    public String getFeeCategory() {
        return feeCategory;
    }

    public void setFeeCategory(String feeCategory) {
        this.feeCategory = feeCategory;
    }

    public List<StandardDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<StandardDocumentDTO> documents) {
        this.documents = documents;
    }

    public List<TestingRequirementDTO> getTestingRequirements() {
        return testingRequirements;
    }

    public void setTestingRequirements(List<TestingRequirementDTO> testingRequirements) {
        this.testingRequirements = testingRequirements;
    }

    public List<String> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<String> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }
}
