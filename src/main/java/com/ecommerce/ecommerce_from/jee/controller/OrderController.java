package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.repository.OrderRepository;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import com.ecommerce.ecommerce_from.jee.repository.UserRepository;
import com.ecommerce.ecommerce_from.jee.service.OrderItemService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> orderData,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf((Integer) orderData.get("userId"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (!user.getUsername().equals(userDetails.getUsername())) {
            throw new IllegalArgumentException("Utilisateur non autorisé");
        }

        List<Map<String, Integer>> items = (List<Map<String, Integer>>) orderData.get("items");
        BigDecimal total = new BigDecimal(orderData.get("total").toString());

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        for (Map<String, Integer> item : items) {
            Long productId = item.get("productId").longValue();
            Integer quantity = item.get("quantity");
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Produit avec ID " + productId + " n'existe pas"));
            orderItemService.addOrderItem(order, product, quantity);
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
}