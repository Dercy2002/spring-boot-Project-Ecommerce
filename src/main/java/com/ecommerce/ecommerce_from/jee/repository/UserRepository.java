package com.ecommerce.ecommerce_from.jee.repository;

import com.ecommerce.ecommerce_from.jee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    public Object findByUsername(String username);

    public Object findByEmail(String username);
}
