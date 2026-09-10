package com.bis.assistant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "certification_schemes")
public class CertificationScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code", unique = true, nullable = false, length = 100)
    private String schemeCode; // e.g. "SCHEME-I", "SCHEME-II-CRS"

    @Column(nullable = false, length = 250)
    private String name;

    @Column(length = 150)
    private String markName; // e.g. "ISI Mark", "Standard Mark (CRS)"

    @Column(name = "target_group", length = 200)
    private String targetGroup;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "audit_requirement", length = 300)
    private String auditRequirement;

    @Column(name = "portal_url", length = 500)
    private String portalUrl;

    public CertificationScheme() {
    }

    public CertificationScheme(String schemeCode, String name, String markName, String targetGroup,
                               String description, String auditRequirement, String portalUrl) {
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
