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
import java.io.InputStream;
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
        logger.info("Creating product with name: {}", request.getName());

        // Validate request
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            logger.error("Product name cannot be null or empty");
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            logger.error("Product description cannot be null or empty");
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            logger.error("Product price must be positive: {}", request.getPrice());
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            logger.error("Product quantity cannot be negative: {}", request.getQuantity());
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }

        // Create product
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        // Handle image
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = saveImage(request.getImage());
                product.setImageUrl(imageUrl);
                logger.info("Image URL set for product: {}", imageUrl);
            } catch (Exception e) {
                logger.error("Failed to process image for product '{}': {}", request.getName(), e.getMessage(), e);
                product.setImageUrl(null); // Continue without image
            }
        } else {
            logger.info("No image provided for product: {}", request.getName());
            product.setImageUrl(null);
        }

        // Save product
        Product savedProduct = productRepository.save(product);
        logger.info("Product saved with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        logger.info("Updating product with id: {}", id);

        // Validate request
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            logger.error("Product name cannot be null or empty");
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            logger.error("Product description cannot be null or empty");
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            logger.error("Product price must be positive: {}", request.getPrice());
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            logger.error("Product quantity cannot be negative: {}", request.getQuantity());
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }

        // Find existing product
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            logger.error("Product not found with id: {}", id);
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        // Update product
        Product product = optionalProduct.get();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        // Handle image
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = saveImage(request.getImage());
                product.setImageUrl(imageUrl);
                logger.info("Image URL updated for product: {}", imageUrl);
            } catch (Exception e) {
                logger.error("Failed to process image for product id {}: {}", id, e.getMessage(), e);
            }
        }

        // Save updated product
        Product savedProduct = productRepository.save(product);
        logger.info("Product updated with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            logger.error("Product not found with id: {}", id);
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        logger.info("Product deleted with id: {}", id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    private String saveImage(MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                logger.error("Uploaded image is null or empty");
                throw new IllegalArgumentException("Le fichier image est vide ou non fourni");
            }

            String contentType = image.getContentType();
            String originalFilename = image.getOriginalFilename();
            logger.info("Processing image: filename={}, contentType={}, size={} bytes",
                    originalFilename != null ? originalFilename : "unknown", contentType, image.getSize());

            if (contentType == null ||
                    !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                logger.error("Invalid file type: {}. Only JPEG and PNG are allowed", contentType);
                throw new IllegalArgumentException("Seules les images JPEG et PNG sont autorisées");
            }

            if (image.getSize() > 5 * 1024 * 1024) {
                logger.error("File size exceeds limit: {} bytes", image.getSize());
                throw new IllegalArgumentException("La taille du fichier dépasse la limite de 5 Mo");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            logger.info("Checking upload directory: {}", uploadPath.toAbsolutePath());

            if (!Files.exists(uploadPath)) {
                logger.info("Creating upload directory: {}", uploadPath.toAbsolutePath());
                Files.createDirectories(uploadPath);
            }

            if (!Files.isWritable(uploadPath)) {
                logger.error("Upload directory is not writable: {}", uploadPath.toAbsolutePath());
                throw new IOException("Le répertoire de destination n'est pas accessible en écriture: " + uploadPath.toAbsolutePath());
            }

            String fileName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "image");
            Path filePath = uploadPath.resolve(fileName);
            logger.info("Saving image to: {}", filePath.toAbsolutePath());

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String imageUrl = IMAGE_BASE_URL + fileName;
            logger.info("Image saved successfully: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            logger.error("Failed to save image: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'enregistrement de l'image: " + e.getMessage());
        }
    }
}