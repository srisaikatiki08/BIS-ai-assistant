package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "testing_requirements")
public class TestingRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id", nullable = false)
    private Standard standard;

    @Column(nullable = false, length = 300)
    private String parameter;

    @Column(name = "test_method", length = 300)
    private String testMethod;

    @Column(name = "acceptance_limit", columnDefinition = "TEXT")
    private String acceptanceLimit;

    public TestingRequirement() {
    }

    public TestingRequirement(String parameter, String testMethod, String acceptanceLimit) {
        this.parameter = parameter;
        this.testMethod = testMethod;
        this.acceptanceLimit = acceptanceLimit;
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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getAcceptanceLimit() {
        return acceptanceLimit;
    }

    public void setAcceptanceLimit(String acceptanceLimit) {
        this.acceptanceLimit = acceptanceLimit;
    }
}
