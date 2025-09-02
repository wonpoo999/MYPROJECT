package com.example.health_dietcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NutritionService {

    @Value("${external.usda.api-key:}")
    private String usdaApiKey;

    private final WebClient.Builder web;

    public record Macros(double cal, double protein, double carb, double fat) {}

    public Macros byFoodName(String name) {
        URI searchUri = UriComponentsBuilder
                .fromUriString("https://api.nal.usda.gov/fdc/v1/foods/search")
                .queryParam("api_key", usdaApiKey)
                .queryParam("query", name)
                .queryParam("pageSize", 5)
                .build(true).toUri();

        Map<String, Object> sres = asMap(web.build()
                .get().uri(searchUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block());

        List<Map<String, Object>> foods = asListOfMaps(sres.get("foods"));
        if (foods.isEmpty()) {
            throw new IllegalStateException("USDA search empty: " + name);
        }

        foods.sort(Comparator.comparingInt(
                f -> distance(name, Objects.toString(f.get("description"), ""))));
        Number fdcId = (Number) foods.get(0).get("fdcId");

        URI detailUri = UriComponentsBuilder
                .fromUriString("https://api.nal.usda.gov/fdc/v1/food/{fdcId}")
                .queryParam("api_key", usdaApiKey)
                .buildAndExpand(fdcId).toUri();

        Map<String, Object> d = asMap(web.build()
                .get().uri(detailUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block());

        Map<String, Double> out = new HashMap<>();

        // labelNutrients 경로 우선
        Map<String, Object> label = asMap(d.get("labelNutrients"));
        if (!label.isEmpty()) {
            Double cal     = toD(asMap(label.get("calories")).get("value"));
            Double protein = toD(asMap(label.get("protein")).get("value"));
            Double carb    = toD(asMap(label.get("carbohydrates")).get("value"));
            Double fat     = toD(asMap(label.get("fat")).get("value"));
            if (cal     != null) out.put("cal", cal);
            if (protein != null) out.put("protein", protein);
            if (carb    != null) out.put("carb", carb);
            if (fat     != null) out.put("fat", fat);
        }

        // label 비었으면 foodNutrients에서 보조 추출
        if (out.isEmpty() && !d.isEmpty()) {
            List<Map<String, Object>> fns = asListOfMaps(d.get("foodNutrients"));
            for (Map<String, Object> n : fns) {
                Map<String, Object> nutrient = asMap(n.get("nutrient"));
                if (nutrient.isEmpty()) continue;

                String nameK = String.valueOf(nutrient.get("name"));
                String unit  = String.valueOf(nutrient.get("unitName"));
                Double amount = toD(n.get("amount"));
                if (amount == null) continue;

                if (nameK.equalsIgnoreCase("Energy") && unit.equalsIgnoreCase("KCAL")) out.put("cal", amount);
                else if (nameK.startsWith("Protein"))          out.put("protein", amount);
                else if (nameK.startsWith("Carbohydrate"))     out.put("carb", amount);
                else if (nameK.startsWith("Total lipid"))      out.put("fat", amount);
            }
        }

        return new Macros(
                out.getOrDefault("cal", 0.0),
                out.getOrDefault("protein", 0.0),
                out.getOrDefault("carb", 0.0),
                out.getOrDefault("fat", 0.0)
        );
    }

    private static Double toD(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); }
        catch (Exception e) { return null; }
    }

    private static int distance(String q, String s) {
        q = q.toLowerCase(); s = s.toLowerCase();
        if (s.contains(q)) return 0;
        return Math.abs(s.length() - q.length());
        // 간단 유사도: 실제로는 Levenshtein 등을 권장
    }

    /** 안전 캐스팅: Object -> Map<String,Object> */
    private static Map<String, Object> asMap(Object src) {
        if (src instanceof Map<?, ?> m) {
            Map<String, Object> out = new HashMap<>();
            m.forEach((k, v) -> out.put(String.valueOf(k), v));
            return out;
        }
        return Collections.emptyMap();
    }

    /** 안전 캐스팅: Object -> List<Map<String,Object>> */
    private static List<Map<String, Object>> asListOfMaps(Object src) {
        if (src instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>(list.size());
            for (Object e : list) out.add(asMap(e));
            return out;
        }
        return Collections.emptyList();
    }
}
