package com.ecommerce.ecommerce_from.jee.repository;

import com.ecommerce.ecommerce_from.jee.entity.Order;
import com.ecommerce.ecommerce_from.jee.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    public List<Order> findByUser(User user);
}
