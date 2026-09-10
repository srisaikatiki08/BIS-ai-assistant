package com.bis.assistant.dto;

public class TestingRequirementDTO {
    private Long id;
    private String parameter;
    private String testMethod;
    private String acceptanceLimit;

    public TestingRequirementDTO() {
    }

    public TestingRequirementDTO(Long id, String parameter, String testMethod, String acceptanceLimit) {
        this.id = id;
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
