package com.example.health_care.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @Getter
public class AiProps {
    @Value("${ai.mode:mock}")           private String mode;            // mock | ollama
    @Value("${ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;
    @Value("${ai.ollama.model:llama3}") private String ollamaModel;

    public boolean useMock(){ return "mock".equalsIgnoreCase(mode); }
}
