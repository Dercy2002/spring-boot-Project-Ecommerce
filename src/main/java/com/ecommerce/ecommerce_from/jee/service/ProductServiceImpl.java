package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private static final String UPLOAD_DIR = "C:/Dev/ecommerce-from-jee/src/main/resources/static/images/";
    private static final String IMAGE_BASE_URL = "/images/";

    // 🔹 Créer un produit
    public Product createProduct(ProductRequest request) {
        validateProductRequest(request);

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                product.setImageUrl(saveImage(image));
            } catch (IOException e) {
                logger.error("Erreur lors de l'enregistrement de l'image : {}", e.getMessage());
                throw new RuntimeException("Erreur lors de l'enregistrement de l'image", e);
            }
        }

        return productRepository.save(product);
    }

    // 🔹 Modifier un produit
    public Product updateProduct(Long id, ProductRequest request) {
        validateProductRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit avec ID " + id + " non trouvé"));

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                product.setImageUrl(saveImage(image));
            } catch (IOException e) {
                logger.error("Erreur lors de la mise à jour de l'image : {}", e.getMessage());
                throw new RuntimeException("Erreur lors de la mise à jour de l'image", e);
            }
        }

        return productRepository.save(product);
    }

    // 🔹 Supprimer un produit
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produit avec ID " + id + " non trouvé");
        }
        productRepository.deleteById(id);
        logger.info("Produit supprimé : ID={}", id);
    }

    // 🔹 Obtenir tous les produits
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 🔹 Obtenir un produit par ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 🔹 Sauvegarder l'image localement et retourner son URL
    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("Image enregistrée à : {}", filePath);
        return IMAGE_BASE_URL + fileName;
    }

    // 🔹 Valider les données du produit
    private void validateProductRequest(ProductRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Le prix doit être supérieur à 0");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
    }
}
