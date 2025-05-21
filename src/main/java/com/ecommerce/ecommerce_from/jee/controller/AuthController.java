package com.ecommerce.ecommerce_from.jee.controller;

import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.dto.AuthRequest;
import com.ecommerce.ecommerce_from.jee.dto.RegisterRequest;
import com.ecommerce.ecommerce_from.jee.enums.Role;
import com.ecommerce.ecommerce_from.jee.payload.AuthResponse;
import com.ecommerce.ecommerce_from.jee.repository.UserRepository;
import com.ecommerce.ecommerce_from.jee.security.JwtTokenProvider;
import com.ecommerce.ecommerce_from.jee.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsernameOrEmail(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role role = Role.USER; // par défaut
        if (registerRequest.getRole() != null) {
            try {
                role = Role.valueOf(registerRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error: Invalid role provided!");
            }
        }

        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
