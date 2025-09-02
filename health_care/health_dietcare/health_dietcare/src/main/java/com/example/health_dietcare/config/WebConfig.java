package com.example.health_dietcare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry reg) {
        reg.addMapping("/**")
           .allowedOrigins(
               "http://localhost:5173",
               "http://localhost:19006",
               "http://127.0.0.1:19006"
           )
           .allowedMethods("*")
           .allowCredentials(true);
    }
}
