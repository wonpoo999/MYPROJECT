package com.example.health_dietcare.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "Health DietCare API", version = "v1", description = "Diet, Goals, Users, Auth, Nutrition")
)
@Configuration
public class OpenApiConfig {}
