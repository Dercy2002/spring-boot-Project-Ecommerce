package com.ecommerce.ecommerce_from.jee.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, JwtAuthenticationFilter jwtFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/products/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/",
                    "/uploads/**",
                    "/index.html",
                    "/login.html",
                    "/register.html",
                    "/admin.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/templates/**",
                    "/static/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; object-src 'none';"))
                .frameOptions(frame -> frame.sameOrigin())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            );

        // Ajout des filtres personnalisés
        http.addFilterBefore(rateLimitingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(xssFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public XSSFilter xssFilter() {
        return new XSSFilter();
    }

    @Bean
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter();
    }
}
