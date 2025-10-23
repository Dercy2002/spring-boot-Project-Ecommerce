package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.entity.Cart;
import com.ecommerce.ecommerce_from.jee.entity.CartItem;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.CartRepository;
import com.ecommerce.ecommerce_from.jee.repository.ProductRepository;
import com.ecommerce.ecommerce_from.jee.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/add")
    public CartItem addCartItem(@RequestParam Long cartId,
                                @RequestParam Long productId,
                                @RequestParam int quantity) {

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        return cartItemService.addCartItem(cart, product, quantity);
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getItemsByCart(@PathVariable Long cartId) {
        return cartItemService.getItemsByCart(cartId);
    }

    @DeleteMapping("/{id}")
    public void removeCartItem(@PathVariable Long id) {
        cartItemService.removeCartItem(id);
    }
}
