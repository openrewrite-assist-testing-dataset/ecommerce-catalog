package com.ecommerce.catalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@ApiModel(description = "Category entity")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @ApiModelProperty(value = "Category ID", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 1, max = 100)
    @JsonProperty
    @ApiModelProperty(value = "Category name", example = "Electronics")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    @JsonProperty
    @ApiModelProperty(value = "Category description", example = "Electronic devices and accessories")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonProperty
    @ApiModelProperty(value = "Parent category")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty
    @ApiModelProperty(value = "Child categories")
    private List<Category> children;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty
    @ApiModelProperty(value = "Products in this category")
    private List<Product> products;
    
    @Column(name = "created_at")
    @JsonProperty
    @ApiModelProperty(value = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonProperty
    @ApiModelProperty(value = "Update timestamp")
    private LocalDateTime updatedAt;

    public Category() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
    
    public List<Category> getChildren() { return children; }
    public void setChildren(List<Category> children) { this.children = children; }
    
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}