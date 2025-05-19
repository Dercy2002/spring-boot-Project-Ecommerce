// --- User.java (corrigé avec enum Role) ---
package com.ecommerce.ecommerce_from.jee.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import com.ecommerce.ecommerce_from.jee.enums.Role; // ✅ Correct


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled = true;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
