package com.ecommerce.ecommerce_from.jee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Value("${api.version:/api/v1}")
    private String apiPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiPrefix, c -> {
            // Applique le préfixe seulement pour les contrôleurs de ton API
            String packageName = c.getPackageName();
            return packageName != null && packageName.startsWith("com.ecommerce.ecommerce_from.jee.controller");
        });
    }
}
