package com.bis.assistant.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String intendedUse;
    private String material;
    private List<String> standardCodes = new ArrayList<>();

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String category, String description, String intendedUse, String material) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.intendedUse = intendedUse;
        this.material = material;
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

    public String getIntendedUse() {
        return intendedUse;
    }

    public void setIntendedUse(String intendedUse) {
        this.intendedUse = intendedUse;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getStandardCodes() {
        return standardCodes;
    }

    public void setStandardCodes(List<String> standardCodes) {
        this.standardCodes = standardCodes;
    }
}
