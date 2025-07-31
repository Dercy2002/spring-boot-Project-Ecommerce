package com.ecommerce.ecommerce_from.jee.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // Ajouter un connecteur HTTP -> HTTPS
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(9090); // Port HTTP
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setRedirectPort(8443); // Redirige vers HTTPS
        tomcat.addAdditionalTomcatConnectors(connector);

        return tomcat;
    }
}
