package com.ecommerce.ecommerce_from.jee.repository;

import com.ecommerce.ecommerce_from.jee.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}