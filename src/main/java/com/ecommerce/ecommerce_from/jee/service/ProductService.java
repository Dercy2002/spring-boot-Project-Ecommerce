package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.dto.ProductRequest;
import com.ecommerce.ecommerce_from.jee.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(ProductRequest request);
    Product updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
}
