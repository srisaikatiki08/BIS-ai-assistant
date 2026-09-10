package com.bis.assistant.dto;

public class LaboratoryDTO {
    private Long id;
    private String name;
    private String type;
    private String city;
    private String state;
    private String address;
    private String googleMapsDirectionsUrl;
    private String nablAccreditation;
    private String contactPerson;
    private String phone;
    private String email;
    private String recognizedStandards;

    public LaboratoryDTO() {
    }

    public LaboratoryDTO(Long id, String name, String type, String city, String state,
                         String address, String googleMapsDirectionsUrl,
                         String nablAccreditation, String contactPerson, String phone,
                         String email, String recognizedStandards) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.city = city;
        this.state = state;
        this.address = address;
        this.googleMapsDirectionsUrl = googleMapsDirectionsUrl;
        this.nablAccreditation = nablAccreditation;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.recognizedStandards = recognizedStandards;
    }

    public LaboratoryDTO(Long id, String name, String type, String city, String state,
                         String nablAccreditation, String contactPerson, String phone,
                         String email, String recognizedStandards) {
        this(id, name, type, city, state, null, null, nablAccreditation, contactPerson, phone, email, recognizedStandards);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGoogleMapsDirectionsUrl() {
        return googleMapsDirectionsUrl;
    }

    public void setGoogleMapsDirectionsUrl(String googleMapsDirectionsUrl) {
        this.googleMapsDirectionsUrl = googleMapsDirectionsUrl;
    }

    public String getNablAccreditation() {
        return nablAccreditation;
    }

    public void setNablAccreditation(String nablAccreditation) {
        this.nablAccreditation = nablAccreditation;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecognizedStandards() {
        return recognizedStandards;
    }

    public void setRecognizedStandards(String recognizedStandards) {
        this.recognizedStandards = recognizedStandards;
    }
}
