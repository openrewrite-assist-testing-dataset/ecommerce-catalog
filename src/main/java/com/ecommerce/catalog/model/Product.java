package com.ecommerce.catalog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@ApiModel(description = "Product entity")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @ApiModelProperty(value = "Product ID", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty
    @ApiModelProperty(value = "Product name", example = "Laptop")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    @JsonProperty
    @ApiModelProperty(value = "Product description", example = "High-performance laptop")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    @JsonProperty
    @ApiModelProperty(value = "Product price", example = "999.99")
    private BigDecimal price;
    
    @Column(nullable = false)
    @NotNull
    @JsonProperty
    @ApiModelProperty(value = "Stock quantity", example = "100")
    private Integer stockQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonProperty
    @ApiModelProperty(value = "Product category")
    private Category category;
    
    @Column(nullable = false)
    @JsonProperty
    @ApiModelProperty(value = "Product status", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    
    @Column(name = "created_at")
    @JsonProperty
    @ApiModelProperty(value = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonProperty
    @ApiModelProperty(value = "Update timestamp")
    private LocalDateTime updatedAt;

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProductStatus.ACTIVE;
    }

    public Product(String name, String description, BigDecimal price, Integer stockQuantity, Category category) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
}