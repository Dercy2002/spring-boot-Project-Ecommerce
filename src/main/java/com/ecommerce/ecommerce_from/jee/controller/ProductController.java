package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;
    private final String uploadDir = "uploads/";

    // ➕ Ajouter un produit avec image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@ModelAttribute ProductRequest productRequest) {
        try {
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setQuantity(productRequest.getQuantity());

            MultipartFile imageFile = productRequest.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/" + uploadDir + fileName);
            }

            Product saved = productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement de l'image.");
        }
    }

    // 🔍 Liste des produits
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // 🔎 Récupérer un produit par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé"));
    }

    // ✏️ Modifier un produit
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé");
        }

        try {
            Product product = optionalProduct.get();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setQuantity(productRequest.getQuantity());

            MultipartFile imageFile = productRequest.getImage();
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/" + uploadDir + fileName);
            }

            productRepository.save(product);
            return ResponseEntity.ok(product);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour de l'image.");
        }
    }

    // 🗑 Supprimer un produit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé");
        }

        productRepository.deleteById(id);
        return ResponseEntity.ok("Produit supprimé avec succès");
    }
}
