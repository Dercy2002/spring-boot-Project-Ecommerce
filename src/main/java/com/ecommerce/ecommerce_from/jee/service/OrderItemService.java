package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.OrderItem;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.OrderItemRepository;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderItem addOrderItem(Order order, Product product, int quantity) {
        if (product == null || product.getPrice() == null) {
            throw new IllegalArgumentException("Le produit ou son prix ne peut pas être null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Produit avec ID " + product.getId() + " n'existe pas");
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(BigDecimal.valueOf(product.getPrice()));
        return orderItemRepository.save(item);
    }

    public List<OrderItem> getItemsByOrder(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .filter(item -> {
                    Product product = item.getProduct();
                    return product != null && productRepository.existsById(product.getId());
                })
                .collect(Collectors.toList());
    }
}