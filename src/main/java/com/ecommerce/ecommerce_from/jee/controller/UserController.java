package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.security.UserDetailsImpl;
import com.ecommerce.ecommerce_from.jee.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Opérations CRUD pour les utilisateurs")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Obtenir l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Créer un nouvel utilisateur")
    @PostMapping
    public ResponseEntity<User> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Les informations de l'utilisateur à créer"
            )
            @RequestBody User user
    ) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Obtenir tous les utilisateurs")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtenir un utilisateur par ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Mettre à jour un utilisateur")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Les nouvelles informations de l'utilisateur"
            )
            @RequestBody User updatedUser
    ) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Supprimer un utilisateur")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
