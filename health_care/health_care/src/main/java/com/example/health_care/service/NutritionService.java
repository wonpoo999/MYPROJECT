package com.example.health_care.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service @RequiredArgsConstructor
public class NutritionService {
    @Value("${usda.apiKey:}") private String usda;
    private final WebClient.Builder web;

    public record Macros(double cal, double protein, double carb, double fat) {}

    public Macros byFoodName(String name){
        URI searchUri = UriComponentsBuilder
                .fromUriString("https://api.nal.usda.gov/fdc/v1/foods/search")
                .queryParam("api_key", usda)
                .queryParam("query", name)
                .queryParam("pageSize", 5)
                .build(true).toUri();

        Map<?,?> sres = web.build().get().uri(searchUri).retrieve().bodyToMono(Map.class).block();
        List<?> foodsRaw = (sres==null)? null : (List<?>) sres.get("foods");
        if (foodsRaw==null || foodsRaw.isEmpty()) throw new IllegalStateException("USDA search empty: " + name);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> foods = (List<Map<String,Object>>) (List<?>) foodsRaw;
        foods.sort(Comparator.comparingInt(f -> distance(name, Objects.toString(f.get("description"), ""))));
        Number fdcId = (Number) foods.get(0).get("fdcId");

        URI detailUri = UriComponentsBuilder
                .fromUriString("https://api.nal.usda.gov/fdc/v1/food/{fdcId}")
                .queryParam("api_key", usda)
                .buildAndExpand(fdcId).toUri();

        Map<?,?> d = web.build().get().uri(detailUri).retrieve().bodyToMono(Map.class).block();

        Map<String,Double> out = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String,Map<String,Object>> label = d==null? null : (Map<String,Map<String,Object>>) d.get("labelNutrients");
        if (label != null) {
            if (label.get("calories")!=null) out.put("cal", toD(label.get("calories").get("value")));
            if (label.get("protein")!=null) out.put("protein", toD(label.get("protein").get("value")));
            if (label.get("carbohydrates")!=null) out.put("carb", toD(label.get("carbohydrates").get("value")));
            if (label.get("fat")!=null) out.put("fat", toD(label.get("fat").get("value")));
        }
        if (out.isEmpty() && d!=null) {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> fns = (List<Map<String,Object>>) d.get("foodNutrients");
            if (fns != null) {
                for (Map<String,Object> n : fns) {
                    Map<String,Object> nutrient = (Map<String,Object>) n.get("nutrient");
                    if (nutrient == null) continue;
                    String nameK = String.valueOf(nutrient.get("name"));
                    String unit = String.valueOf(nutrient.get("unitName"));
                    Double amount = toD(n.get("amount"));
                    if (amount == null) continue;
                    if (nameK.equalsIgnoreCase("Energy") && unit.equalsIgnoreCase("KCAL")) out.put("cal", amount);
                    else if (nameK.startsWith("Protein")) out.put("protein", amount);
                    else if (nameK.startsWith("Carbohydrate")) out.put("carb", amount);
                    else if (nameK.startsWith("Total lipid")) out.put("fat", amount);
                }
            }
        }
        return new Macros(out.getOrDefault("cal",0.0), out.getOrDefault("protein",0.0),
                          out.getOrDefault("carb",0.0), out.getOrDefault("fat",0.0));
    }

    private static Double toD(Object o){ return (o instanceof Number)? ((Number)o).doubleValue() : null; }
    private static int distance(String q, String s) {
        q = q.toLowerCase(); s = s.toLowerCase();
        if (s.contains(q)) return 0;
        return Math.abs(s.length() - q.length());
    }
}
