package com.example.health_dietcare.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProps {
    private String mode = "mock";

    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3";
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }
    private Ollama ollama = new Ollama();

    public boolean useMock() { return "mock".equalsIgnoreCase(mode); }
    public String getOllamaBaseUrl() { return ollama.getBaseUrl(); }
    public String getOllamaModel() { return ollama.getModel(); }
}
