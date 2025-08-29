package com.example.health_care.controller;

import com.example.health_care.dto.AiDtos;
import com.example.health_care.dto.FoodDtos;
import com.example.health_care.service.AiService;
import com.example.health_care.service.FoodRecognitionService;
import com.example.health_care.service.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foodlogs")
public class FoodLogController {
    private final FoodRecognitionService vision;
    private final NutritionService nutrition;
    private final AiService ai;

    // 사진 업로드 → Clarifai 분류 → (모호하면 후보 반환) → USDA 영양치
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FoodDtos.LogRes> upload(@RequestPart("file") FilePart file){
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"), file.filename());
        return file.transferTo(tmp).then(Mono.fromCallable(() -> {
            byte[] bytes = Files.readAllBytes(tmp);
            List<FoodDtos.Candidate> top = vision.classify(bytes);

            if (!vision.isAcceptable(top)) {
                return new FoodDtos.LogRes("ambiguous", null, null, null, null, null,
                        top.subList(0, Math.min(3, top.size())));
            }

            String foodName = top.get(0).getName();
            NutritionService.Macros m = nutrition.byFoodName(foodName);
            return new FoodDtos.LogRes("ok", foodName, m.cal(), m.protein(), m.carb(), m.fat(), null, null);
        }));
    }

    // 모호 시 클라이언트가 최종 음식명 확정
    @PostMapping("/confirm")
    public FoodDtos.LogRes confirm(@RequestBody FoodDtos.ConfirmReq req){
        NutritionService.Macros m = nutrition.byFoodName(req.getFoodName());
        return new FoodDtos.LogRes("ok", req.getFoodName(), m.cal(), m.protein(), m.carb(), m.fat(), req.getImgUrl(), null);
    }

    // 영양 → AI 평가(약/중립/독 + 스탯 변화)
    @PostMapping("/evaluate")
    public AiDtos.FoodEvalRes evaluate(@RequestBody AiDtos.FoodEvalReq r){
        return ai.evaluateFood(r);
    }
}
