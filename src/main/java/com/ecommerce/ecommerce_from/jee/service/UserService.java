package com.ecommerce.ecommerce_from.jee.service;

import com.ecommerce.ecommerce_from.jee.dto.RegisterRequest;
import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.enums.Role;
import com.ecommerce.ecommerce_from.jee.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔐 Enregistrement avec RegisterRequest
    public User registerUser(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("Le nom d'utilisateur est déjà utilisé");
        }

        if (existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email est déjà utilisé");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase())); // admin, user, etc.
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // 🔄 Création directe d'un utilisateur (sans DTO)
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // 📜 Tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔍 Un utilisateur par ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // 🔁 Mise à jour utilisateur
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }

    // ❌ Suppression
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 🔍 Recherches par username ou email
    public Optional<User> findByUsername(String username) {
        return (Optional<User>) userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        String username = null;
        return userRepository.existsByEmail(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
