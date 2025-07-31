package com.ecommerce.ecommerce_from.jee.dto;

import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private MultipartFile image; // Ce champ est optionnel

    // === Getters & Setters ===

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    // === Constructeurs ===

    public ProductRequest() {
        // Aucun champ requis ici
    }

    public ProductRequest(String name, String description, Double price, Integer quantity, MultipartFile image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.image = image; // Peut être null sans erreur
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", image=" + (image != null ? image.getOriginalFilename() : "Aucune image") +
                '}';
    }
}
