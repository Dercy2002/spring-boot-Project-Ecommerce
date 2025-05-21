package com.ecommerce.ecommerce_from.jee.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private Double price;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantity;

    private MultipartFile image;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}