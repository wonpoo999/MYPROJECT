package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.FoodDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodRecognitionService {
    @Value("${external.clarifai.api-key:}") private String clarifaiApiKey;
    @Value("${external.clarifai.model-id:food-item-recognition}") private String modelId;

    private final WebClient.Builder web;

    public List<FoodDtos.Candidate> classify(byte[] imageBytes) {
        String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);

        Map<String,Object> body = Map.of(
                "inputs", List.of(Map.of(
                        "data", Map.of(
                                "image", Map.of("base64", base64)
                        )
                ))
        );

        Map<?,?> resp = web.build()
                .post().uri("https://api.clarifai.com/v2/models/{modelId}/outputs", modelId)
                .header("Authorization", "Key " + clarifaiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp==null) return List.of();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> outputs = (List<Map<String,Object>>) resp.get("outputs");
        if (outputs==null || outputs.isEmpty()) return List.of();

        @SuppressWarnings("unchecked")
        Map<String,Object> data = (Map<String,Object>) outputs.get(0).get("data");
        if (data==null) return List.of();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> concepts = (List<Map<String,Object>>) data.get("concepts");
        if (concepts==null) return List.of();

        return concepts.stream()
                .map(c -> new FoodDtos.Candidate(
                        String.valueOf(c.getOrDefault("name","")),
                        toD(c.get("value"))
                ))
                .sorted(Comparator.comparingDouble(FoodDtos.Candidate::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public boolean isAcceptable(List<FoodDtos.Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) return false;
        double top = candidates.get(0).getScore();
        double second = (candidates.size() > 1) ? candidates.get(1).getScore() : 0.0;
        return top >= 0.75 && (top - second) >= 0.10;
    }

    private static double toD(Object o){
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0.0; }
    }
}
