package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.OrderItem;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.OrderItemRepository;
import com.ecommerce.ecommerce_from.jee.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public OrderItem addOrderItem(Order order, Product product, int quantity) {
        logger.info("Adding order item for order ID: {}, product ID: {}, quantity: {}", 
                order != null ? order.getId() : null, 
                product != null ? product.getId() : null, 
                quantity);

        // Validation
        if (order == null) {
            logger.error("Order cannot be null");
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (product == null) {
            logger.error("Product cannot be null");
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            logger.error("Quantity must be positive: {}", quantity);
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (product.getPrice() == null) {
            logger.error("Product price cannot be null for product ID: {}", product.getId());
            throw new IllegalArgumentException("Product price cannot be null");
        }

        // Create order item
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        // Convert Double to BigDecimal
        item.setPrice(BigDecimal.valueOf(product.getPrice())); // Unit price of the product

        // Save order item
        OrderItem savedItem = orderItemRepository.save(item);
        logger.info("Order item saved with ID: {}", savedItem.getId());

        // Update order total
        updateOrderTotal(order);

        return savedItem;
    }

    public List<OrderItem> getItemsByOrder(Long orderId) {
        logger.info("Fetching order items for order ID: {}", orderId);
        if (orderId == null) {
            logger.error("Order ID cannot be null");
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        logger.info("Found {} order items for order ID: {}", items.size(), orderId);
        return items;
    }

    @Transactional
    private void updateOrderTotal(Order order) {
        logger.info("Updating total for order ID: {}", order.getId());
        if (order == null || order.getId() == null) {
            logger.error("Order or order ID cannot be null");
            throw new IllegalArgumentException("Order or order ID cannot be null");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        BigDecimal total = items.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    BigDecimal price = item.getPrice();
                    int quantity = item.getQuantity();
                    if (price == null) {
                        logger.warn("Price is null for order item ID: {}", item.getId());
                        return BigDecimal.ZERO;
                    }
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);
        orderRepository.save(order);
        logger.info("Order ID: {} updated with total: {}", order.getId(), total);
    }
}