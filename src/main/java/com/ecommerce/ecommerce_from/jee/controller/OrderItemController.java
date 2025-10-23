package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.OrderItem;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.OrderRepository;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import com.ecommerce.ecommerce_from.jee.service.OrderItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/add")
    public ResponseEntity<OrderItem> addItem(
            @RequestParam Long orderId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem newItem = (OrderItem) orderItemService.addOrderItem(order, product, quantity);

        return ResponseEntity.ok(newItem);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getItemsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getItemsByOrder(orderId));
    }
}
