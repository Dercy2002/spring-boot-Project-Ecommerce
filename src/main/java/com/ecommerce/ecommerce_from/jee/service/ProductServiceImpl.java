package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = "C:/Dev/ecommerce-from-jee/src/main/resources/static/images/";
    private static final String IMAGE_BASE_URL = "/images/";

    @Override
    public Product createProduct(ProductRequest request) {
        logger.info("Création produit: nom='{}', description='{}', prix={}, quantité={}, image={}",
                request.getName(), request.getDescription(), request.getPrice(), request.getQuantity(),
                request.getImage() != null ? request.getImage().getOriginalFilename() : "aucune");

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            logger.error("Nom produit vide/null: '{}'", request.getName());
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide ou null");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            logger.error("Prix invalide: {}", request.getPrice());
            throw new IllegalArgumentException("Le prix doit être supérieur à 0");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            logger.error("Quantité invalide: {}", request.getQuantity());
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = saveImage(request.getImage());
                product.setImageUrl(imageUrl);
                logger.info("Image URL: {}", imageUrl);
            } catch (IOException e) {
                logger.error("Échec traitement image: {}", e.getMessage());
                product.setImageUrl(null);
            }
        } else {
            logger.info("Aucune image fournie");
            product.setImageUrl(null);
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Produit enregistré: ID={}, nom={}, image_url={}", savedProduct.getId(), savedProduct.getName(), savedProduct.getImageUrl());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        logger.info("Mise à jour produit ID={}: nom='{}', prix={}, quantité={}, image={}",
                id, request.getName(), request.getPrice(), request.getQuantity(),
                request.getImage() != null ? request.getImage().getOriginalFilename() : "aucune");

        if (id == null) {
            logger.error("ID produit null");
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            logger.error("Nom produit vide/null: '{}'", request.getName());
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide ou null");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            logger.error("Prix invalide: {}", request.getPrice());
            throw new IllegalArgumentException("Le prix doit être supérieur à 0");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            logger.error("Quantité invalide: {}", request.getQuantity());
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            logger.error("Produit ID={} non trouvé", id);
            throw new IllegalArgumentException("Produit avec ID " + id + " non trouvé");
        }

        Product product = optionalProduct.get();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = saveImage(request.getImage());
                product.setImageUrl(imageUrl);
                logger.info("Image mise à jour: {}", imageUrl);
            } catch (IOException e) {
                logger.error("Échec traitement image mise à jour: {}", e.getMessage());
                product.setImageUrl(product.getImageUrl());
            }
        }

        Product updatedProduct = productRepository.save(product);
        logger.info("Produit mis à jour: ID={}, nom={}, image_url={}", updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getImageUrl());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Suppression produit ID={}", id);

        if (id == null) {
            logger.error("ID produit null");
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            logger.error("Produit ID={} non trouvé", id);
            throw new IllegalArgumentException("Produit avec ID " + id + " non trouvé");
        }

        productRepository.deleteById(id);
        logger.info("Produit ID={} supprimé", id);
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            logger.info("Récupération {} produits", products.size());
            products.forEach(p -> logger.debug("Produit: id={}, nom={}, image_url={}, description={}",
                    p.getId(), p.getName(), p.getImageUrl(), p.getDescription()));
            return products;
        } catch (Exception e) {
            logger.error("Erreur récupération produits: {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération des produits", e);
        }
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        logger.info("Récupération produit ID={}", id);

        if (id == null) {
            logger.error("ID produit null");
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                logger.info("Produit trouvé: ID={}, nom={}, image_url={}", product.get().getId(), product.get().getName(), product.get().getImageUrl());
            } else {
                logger.info("Produit ID={} non trouvé", id);
            }
            return product;
        } catch (Exception e) {
            logger.error("Erreur récupération produit ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Échec de la récupération du produit", e);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.isWritable(filePath.getParent())) {
                logger.error("Répertoire non accessible en écriture: {}", filePath.getParent());
                throw new IOException("Répertoire non accessible en écriture: " + filePath.getParent());
            }
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image enregistrée: chemin={}", filePath);
            String imageUrl = IMAGE_BASE_URL + fileName;
            logger.info("Image URL générée: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            logger.error("Échec enregistrement image: chemin={}, erreur={}", filePath, e.getMessage());
            throw e;
        }
    }
}