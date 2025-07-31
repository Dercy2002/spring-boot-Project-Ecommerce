package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.dto.LoginRequest;
import com.ecommerce.ecommerce_from.jee.dto.RegisterRequest;
import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.enums.Role;
import com.ecommerce.ecommerce_from.jee.security.JwtTokenProvider;
import com.ecommerce.ecommerce_from.jee.service.UserService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role role = Role.USER;
        if (registerRequest.getRole() != null) {
            try {
                role = Role.valueOf(registerRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error: Invalid role provided!");
            }
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        userService.saveUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok("Bearer " + token);
    }
}
