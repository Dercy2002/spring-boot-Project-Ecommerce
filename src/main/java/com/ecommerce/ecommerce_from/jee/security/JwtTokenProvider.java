package com.ecommerce.ecommerce_from.jee.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Classe utilitaire pour générer, valider et extraire des informations des tokens JWT.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms}")
    private long jwtExpirationMs;

    private Key key;

    /**
     * Initialise la clé secrète après l'injection des propriétés.
     */
    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("La clé jwtSecret doit faire au moins 32 caractères");
        }
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT à partir de l'objet Authentication.
     * 
     * @param authentication l'objet Authentication de Spring Security
     * @return le token JWT signé
     */
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())   // username dans le sujet (sub)
                .claim("userId", userDetails.getId())    // id utilisateur en claim personnalisé
                .setIssuedAt(now)                        // date émission
                .setExpiration(expiryDate)               // date expiration
                .signWith(key, SignatureAlgorithm.HS256) // signature HMAC SHA256 avec la clé
                .compact();
    }

    /**
     * Extrait le username depuis un token JWT valide.
     * 
     * @param token le JWT
     * @return le username (subject)
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrait l'id utilisateur depuis un token JWT valide.
     * 
     * @param token le JWT
     * @return l'id utilisateur (Long)
     */
    public Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    /**
     * Valide un token JWT (signature, expiration, format).
     * 
     * @param token le JWT
     * @return true si valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.err.println("Token JWT invalide: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expiré: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT non supporté: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token JWT vide ou incorrect: " + e.getMessage());
        }
        return false;
    }
}
