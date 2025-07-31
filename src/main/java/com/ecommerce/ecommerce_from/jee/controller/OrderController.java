package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.dto.OrderRequest;
import com.ecommerce.ecommerce_from.jee.dto.OrderRequest.OrderItemRequest;
import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.repository.OrderRepository;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import com.ecommerce.ecommerce_from.jee.repository.UserRepository;
import com.ecommerce.ecommerce_from.jee.service.OrderItemService;
import com.ecommerce.ecommerce_from.jee.security.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemService orderItemService;

    // Création de commande
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            return ResponseEntity.status(401).body("Utilisateur non authentifié");
        }

        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Utilisateur introuvable");
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotal(request.getTotal());
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("Produit avec ID " + item.getProductId() + " introuvable");
            }
            orderItemService.addOrderItem(order, product, item.getQuantity());
        }

        return ResponseEntity.ok(order);
    }

    // Liste des commandes de l'utilisateur connecté
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Order> orders = orderRepository.findByUserId(userDetails.getId());
        return ResponseEntity.ok(orders);
    }

    // Suppression d'une commande par ID (seulement si elle appartient à l'utilisateur)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Commande non trouvée");
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).body("Vous n'êtes pas autorisé à supprimer cette commande");
        }

        orderRepository.delete(order);
        return ResponseEntity.ok("Commande supprimée avec succès");
    }
}
