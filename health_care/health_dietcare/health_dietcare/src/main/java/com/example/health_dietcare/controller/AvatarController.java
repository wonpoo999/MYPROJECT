package com.example.health_dietcare.controller;

import com.example.health_dietcare.dto.AvatarDtos;
import com.example.health_dietcare.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/avatar")
public class AvatarController {
    private final AvatarService avatar;

    @PostMapping
    public AvatarDtos.AvRes create(Authentication auth,
                                   @RequestBody(required = false) AvatarDtos.CreateReq r){
        return avatar.createOrGet(auth, (r == null ? new AvatarDtos.CreateReq() : r));
    }
}
