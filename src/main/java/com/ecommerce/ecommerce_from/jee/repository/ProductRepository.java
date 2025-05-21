package com.ecommerce.ecommerce_from.jee.repository;

import com.ecommerce.ecommerce_from.jee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}