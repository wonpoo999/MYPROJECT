package com.example.health_dietcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.health_dietcare.dto.AvatarDtos;
import com.example.health_dietcare.service.AvatarService;

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
