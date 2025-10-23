package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final String uploadDir = "uploads/"; // Dossier local d'enregistrement des images

    // 🔹 Ajouter produit
    public ResponseEntity<?> createProduct(ProductRequest request) {
        String imageUrl = null;

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = saveImage(request.getImage());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(imageUrl); // Peut être null

        productRepository.save(product);
        return ResponseEntity.ok("Produit ajouté avec succès");
    }

    // 🔹 Liste
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // 🔹 Supprimer
    public ResponseEntity<?> deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Produit supprimé");
        }
        return ResponseEntity.status(404).body("Produit non trouvé");
    }

    // 🔹 Modifier produit
    public ResponseEntity<?> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id).map(product -> {
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());

            MultipartFile image = request.getImage();
            if (image != null && !image.isEmpty()) {
                product.setImageUrl(saveImage(image));
            }

            productRepository.save(product);
            return ResponseEntity.ok("Produit mis à jour");
        }).orElse(ResponseEntity.status(404).body("Produit non trouvé"));
    }

    // 🔹 Méthode pour enregistrer l’image
    private String saveImage(MultipartFile image) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + image.getOriginalFilename();
            File file = new File(filePath);
            image.transferTo(file);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'image", e);
        }
    }
}
