package com.bis.assistant.dto;

public class CertificationSchemeDTO {
    private Long id;
    private String schemeCode;
    private String name;
    private String markName;
    private String targetGroup;
    private String description;
    private String auditRequirement;
    private String portalUrl;

    public CertificationSchemeDTO() {
    }

    public CertificationSchemeDTO(Long id, String schemeCode, String name, String markName,
                                  String targetGroup, String description, String auditRequirement, String portalUrl) {
        this.id = id;
        this.schemeCode = schemeCode;
        this.name = name;
        this.markName = markName;
        this.targetGroup = targetGroup;
        this.description = description;
        this.auditRequirement = auditRequirement;
        this.portalUrl = portalUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuditRequirement() {
        return auditRequirement;
    }

    public void setAuditRequirement(String auditRequirement) {
        this.auditRequirement = auditRequirement;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }
}
