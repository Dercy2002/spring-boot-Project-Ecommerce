package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.entity.Cart;
import com.ecommerce.ecommerce_from.jee.entity.CartItem;
import com.ecommerce.ecommerce_from.jee.entity.Product;
import com.ecommerce.ecommerce_from.jee.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartItem addCartItem(Cart cart, Product product, int quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public List<CartItem> getItemsByCart(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public void removeCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}
