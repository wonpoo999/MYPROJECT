package com.example.health_dietcare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * application.yml:
 * app:
 *   cors:
 *     allowed-origins:
 *       - http://localhost:5173
 *       - http://localhost:19006
 *       - http://127.0.0.1:19006
 */
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProps {

    private List<String> allowedOrigins = new ArrayList<>();

    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
}
