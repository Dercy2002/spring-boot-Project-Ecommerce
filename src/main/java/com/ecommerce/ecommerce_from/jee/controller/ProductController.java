package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.service.ProductService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            logger.info("Retour de {} produits", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des produits: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur serveur lors de la récupération des produits: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            logger.info("Requête de création reçue: productJson={}", productJson);
            // Mapper manuellement productJson à ProductRequest
            ProductRequest request = new ProductRequest();
            // Supposons que productJson est {"name":"...","description":"...","price":...,"quantity":...}
            // Pour simplifier, utiliser un parseur JSON (par exemple, Jackson)
            org.springframework.boot.json.JacksonJsonParser parser = new org.springframework.boot.json.JacksonJsonParser();
            var map = parser.parseMap(productJson);
            request.setName((String) map.get("name"));
            request.setDescription((String) map.get("description"));
            request.setPrice(map.get("price") != null ? Double.valueOf(map.get("price").toString()) : null);
            request.setQuantity(map.get("quantity") != null ? Integer.valueOf(map.get("quantity").toString()) : null);
            request.setImage(image);

            Product product = productService.createProduct(request);
            logger.info("Produit créé: ID={}, image_url={}", product.getId(), product.getImageUrl());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Erreur serveur création produit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur serveur lors de la création du produit: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ProductRequest request = new ProductRequest();
            org.springframework.boot.json.JacksonJsonParser parser = new org.springframework.boot.json.JacksonJsonParser();
            var map = parser.parseMap(productJson);
            request.setName((String) map.get("name"));
            request.setDescription((String) map.get("description"));
            request.setPrice(map.get("price") != null ? Double.valueOf(map.get("price").toString()) : null);
            request.setQuantity(map.get("quantity") != null ? Integer.valueOf(map.get("quantity").toString()) : null);
            request.setImage(image);

            Product updatedProduct = productService.updateProduct(id, request);
            logger.info("Produit mis à jour: ID={}", updatedProduct.getId());
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur mise à jour ID={}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Erreur serveur mise à jour ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur serveur lors de la mise à jour du produit: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            logger.info("Produit supprimé: ID={}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Erreur suppression ID={}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Erreur serveur suppression ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur serveur lors de la suppression du produit: " + e.getMessage() + "\"}");
        }
    }
}