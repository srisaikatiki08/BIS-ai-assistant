package com.bis.assistant.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(length = 150)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "intended_use", columnDefinition = "TEXT")
    private String intendedUse;

    @Column(length = 200)
    private String material;

    @ManyToMany
    @JoinTable(
            name = "product_standards",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "standard_id")
    )
    private List<Standard> standards = new ArrayList<>();

    public Product() {
    }

    public Product(String name, String category, String description, String intendedUse, String material) {
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

    public List<Standard> getStandards() {
        return standards;
    }

    public void setStandards(List<Standard> standards) {
        this.standards = standards;
    }

    public void addStandard(Standard standard) {
        this.standards.add(standard);
    }
}
