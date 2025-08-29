package com.example.health_care.controller;

import com.example.health_care.dto.AvatarDtos;
import com.example.health_care.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/avatar")
public class AvatarController {
    private final AvatarService avatar;

    @PostMapping
    public AvatarDtos.AvRes create(Authentication auth, @RequestBody AvatarDtos.CreateReq r){
        return avatar.createOrGet(auth, r);
    }
}
