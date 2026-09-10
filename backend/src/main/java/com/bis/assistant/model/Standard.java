package com.bis.assistant.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "standards")
public class Standard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "standard_code", unique = true, nullable = false, length = 100)
    private String standardCode; // e.g. "IS-4151-2015"

    @Column(name = "is_number", nullable = false, length = 100)
    private String isNumber; // e.g. "IS 4151 : 2015"

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 200)
    private String division;

    @Column(length = 150)
    private String category;

    @Column(name = "publication_year", length = 20)
    private String year;

    @Column(length = 100)
    private String status;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;

    @Column(length = 200)
    private String scheme;

    @Column(name = "qco_order", length = 500)
    private String qcoOrder;

    @Column(columnDefinition = "TEXT")
    private String scope;

    @Column(name = "licensing_process", columnDefinition = "TEXT")
    private String licensingProcess;

    @Column(name = "fee_category", length = 250)
    private String feeCategory;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StandardDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TestingRequirement> testingRequirements = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "standard_applicable_products", joinColumns = @JoinColumn(name = "standard_id"))
    @Column(name = "product_name")
    private List<String> applicableProducts = new ArrayList<>();

    public Standard() {
    }

    public Standard(String standardCode, String isNumber, String title, String division, String category,
                    String year, String status, Boolean isMandatory, String scheme, String qcoOrder,
                    String scope, String licensingProcess, String feeCategory) {
        this.standardCode = standardCode;
        this.isNumber = isNumber;
        this.title = title;
        this.division = division;
        this.category = category;
        this.year = year;
        this.status = status;
        this.isMandatory = isMandatory != null ? isMandatory : false;
        this.scheme = scheme;
        this.qcoOrder = qcoOrder;
        this.scope = scope;
        this.licensingProcess = licensingProcess;
        this.feeCategory = feeCategory;
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

    public void setIsMandatory(Boolean mandatory) {
        isMandatory = mandatory;
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

    public List<StandardDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<StandardDocument> documents) {
        this.documents = documents;
    }

    public void addDocument(StandardDocument document) {
        documents.add(document);
        document.setStandard(this);
    }

    public List<TestingRequirement> getTestingRequirements() {
        return testingRequirements;
    }

    public void setTestingRequirements(List<TestingRequirement> testingRequirements) {
        this.testingRequirements = testingRequirements;
    }

    public void addTestingRequirement(TestingRequirement testingRequirement) {
        testingRequirements.add(testingRequirement);
        testingRequirement.setStandard(this);
    }

    public List<String> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<String> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public void addApplicableProduct(String product) {
        this.applicableProducts.add(product);
    }
}
