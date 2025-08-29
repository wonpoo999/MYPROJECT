package com.example.health_care.service;

import com.example.health_care.dto.FoodDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class FoodRecognitionService {
    @Value("${clarifai.apiKey:}") private String clarifai;
    private final WebClient.Builder web;

    private static final double THRESH_ACCEPT = 0.65;
    private static final double THRESH_MARGIN = 0.10;

    public List<FoodDtos.Candidate> classify(byte[] imageBytes) {
        String base64 = Base64Utils.encodeToString(imageBytes);
        Map<String,Object> body = Map.of("inputs", List.of(Map.of("data", Map.of("image", Map.of("base64", base64)))));
        Map<?,?> resp = web.build()
                .post().uri("https://api.clarifai.com/v2/models/food-item-recognition/outputs")
                .header("Authorization", "Key " + clarifai)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body).retrieve().bodyToMono(Map.class).block();

        if (resp==null) return List.of();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> outputs = (List<Map<String,Object>>) resp.get("outputs");
        if (outputs==null || outputs.isEmpty()) return List.of();

        Map<String,Object> first = outputs.get(0);
        @SuppressWarnings("unchecked")
        Map<String,Object> data = (Map<String,Object>) first.get("data");
        if (data==null) return List.of();

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> concepts = (List<Map<String,Object>>) data.get("concepts");
        if (concepts==null) return List.of();

        return concepts.stream()
                .map(c -> new FoodDtos.Candidate(
                        String.valueOf(c.getOrDefault("name","")),
                        toD(c.get("value"))
                ))
                .sorted(Comparator.comparingDouble(FoodDtos.Candidate::getScore).
