// src/main/java/com/example/health_dietcare/service/CharacterService.java
package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.AiDtos;
import com.example.health_dietcare.dto.AvatarDtos;
import com.example.health_dietcare.entity.Avatar;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.AvatarRepository;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CharacterService {
    private final AvatarRepository avatars;
    private final UserRepository users;

    public Avatar ensure(Authentication auth) {
        User u = users.findByUsername(auth.getName()).orElseThrow();
        return avatars.findByUser(u).orElseGet(() -> avatars.save(
            Avatar.builder().user(u).nickname(u.getUsername()).build()
        ));
    }

    public Avatar applyEval(Avatar a, AiDtos.FoodEvalRes eval){
        // 간단 성장 규칙 [ADDED]
        int hp = Math.max(0, a.getHp() + eval.getDelta().getHp());
        int atk = Math.max(1, a.getAtk() + eval.getDelta().getAtk());
        int def = Math.max(1, a.getDef() + eval.getDelta().getDef());
        int exp = a.getExp() + Math.max(1, eval.getScore()/10);

        int level = a.getLevel();
        while (exp >= 100) { level++; exp -= 100; hp += 2; atk += 1; def += 1; }

        a.setHp(hp); a.setAtk(atk); a.setDef(def); a.setExp(exp); a.setLevel(level);
        return avatars.save(a);
    }

    public AvatarDtos.AvRes toRes(Avatar a){
        return new AvatarDtos.AvRes(a.getNickname(), a.getLevel(), a.getHp(), a.getAtk(), a.getDef(), a.getExp());
    }
}
