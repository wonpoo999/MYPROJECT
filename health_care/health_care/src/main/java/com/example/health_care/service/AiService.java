package com.example.health_care.service;

import com.example.health_care.config.AiProps;
import com.example.health_care.dto.AiDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiProps props;
    private final WebClient.Builder webClientBuilder;

    public String summarize(String text){
        if (props.useMock()) return ruleSummary(text);
        String prompt = "아래 내용을 한국어로 3줄 핵심 불릿으로 요약해줘.\n\n" + text;
        return ollamaGenerate(prompt);
    }

    public List<String> extractTodos(String text){
        if (props.useMock()) return ruleTodos(text);
        String prompt = """
                Extract up to 5 actionable TODO items from the following text.
                Return plain lines without numbering or extra text. Use Korean.
                ---
                %s
                """.formatted(text);
        String out = ollamaGenerate(prompt);
        return Arrays.stream(out.split("\\r?\\n"))
                .map(s->s.replaceFirst("^[-*]\\s*","").trim())
                .filter(s->!s.isEmpty())
                .limit(5)
                .collect(Collectors.toList());
    }

    public AiDtos.FoodEvalRes evaluateFood(AiDtos.FoodEvalReq r){
        double score=0;
        int pTarget = (r.getDailyProteinTarget()!=null && r.getDailyProteinTarget()>0) ? r.getDailyProteinTarget() : 75;
        score += Math.min(r.getProtein()/Math.max(10,pTarget)*40, 40);

        double cal = Math.max(1, r.getCal());
        double fatDensity = (r.getFat()/cal)*100.0;
        score += Math.min(15, Math.max(0, 15 - fatDensity));

        double sugarPenalty = Math.min(30, r.getCarb()*0.5);
        score = Math.max(0, Math.min(100, score - sugarPenalty));

        String label = score>=75? "약" : score>=50? "중립" : "독";
        String comment = switch (label){
            case "약" -> "단백질과 영양 밸런스가 좋아요. 채소를 곁들이면 더 좋습니다.";
            case "중립" -> "무난하지만 당/지방을 조금 낮추면 더 좋아요.";
            default -> "당/지방 밀도가 높아요. 양 조절 또는 대체 메뉴를 추천합니다.";
        };
        AiDtos.StatDelta delta = new AiDtos.StatDelta(
                label.equals("독")? -5 : label.equals("약")? +5 : 0,
                (int)Math.round(r.getProtein()/10.0),
                label.equals("약")? +2 : 0
        );
        return new AiDtos.FoodEvalRes(label, (int)Math.round(score), comment, delta);
    }

    /* ---------- 내부 ---------- */
    private String ollamaGenerate(String prompt){
        try{
            Map<String,Object> body = Map.of(
                    "model", props.getOllamaModel(),
                    "prompt", prompt,
                    "stream", false
            );
            Map<?,?> res = webClientBuilder.build()
                    .post().uri(props.getOllamaBaseUrl() + "/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Object response = (res==null)? null : res.get("response");
            return response==null ? "" : response.toString();
        }catch(Exception e){
            return "[LLM 호출 실패 → mock 사용]\n" + ruleSummary(prompt);
        }
    }

    private String ruleSummary(String t){
        String[] s = t.split("[.!?\\n]+");
        StringBuilder sb = new StringBuilder(); int c=0;
        for(String p : s){
            p = p.trim();
            if (p.isEmpty()) continue;
            sb.append("• ").append(p).append("\n");
            if (++c >= 3) break;
        }
        return c==0 ? "• 요약할 문장이 없습니다." : sb.toString().trim();
    }

    private List<String> ruleTodos(String t){
        List<String> out = new ArrayList<>();
        for(String l : t.split("[\\n；;\\.]")){
            l = l.trim(); if (l.isEmpty()) continue;
            out.add(l.replaceAll("^(해야 할|할 것|TODO|todo)[:：]\\s*",""));
            if (out.size()>=5) break;
        }
        if (out.isEmpty()) out.add("목표를 한 줄로 적고 다시 요청하세요.");
        return out;
    }
}
