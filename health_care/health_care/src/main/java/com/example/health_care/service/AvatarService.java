package com.example.health_care.service;

import com.example.health_care.dto.AvatarDtos;
import com.example.health_care.entity.Avatar;
import com.example.health_care.entity.User;
import com.example.health_care.repository.AvatarRepository;
import com.example.health_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatars;
    private final UserRepository users;

    public AvatarDtos.AvRes createOrGet(Authentication auth, AvatarDtos.CreateReq r){
        User u = users.findByUsername(auth.getName()).orElseThrow();
        Avatar a = avatars.findByUser(u).orElseGet(() -> avatars.save(Avatar.builder().user(u).nickname(r.getNickname()).build()));
        if (r.getNickname()!=null && !r.getNickname().isBlank()) { a.setNickname(r.getNickname()); avatars.save(a); }
        return new AvatarDtos.AvRes(a.getNickname(), a.getLevel(), a.getHp(), a.getAtk(), a.getDef(), a.getExp());
    }
}
