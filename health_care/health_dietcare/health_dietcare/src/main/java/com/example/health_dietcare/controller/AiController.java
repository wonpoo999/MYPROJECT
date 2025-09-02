package com.example.health_dietcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.health_dietcare.dto.AiDtos;
import com.example.health_dietcare.service.AiService;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {
    private final AiService svc;

    @PostMapping("/summary")
    public AiDtos.SummaryRes summarize(@RequestBody AiDtos.SummaryReq req){
        return new AiDtos.SummaryRes(svc.summarize(req.getText()));
    }

    @PostMapping("/todos")
    public AiDtos.TodoRes todos(@RequestBody AiDtos.TodoReq req){
        List<String> list = svc.extractTodos(req.getText());
        return new AiDtos.TodoRes(list);
    }

    @PostMapping("/food-eval")
    public AiDtos.FoodEvalRes foodEval(@RequestBody AiDtos.FoodEvalReq req){
        return svc.evaluateFood(req);
    }
}
