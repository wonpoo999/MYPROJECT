// src/main/java/com/example/health_dietcare/controller/CharacterController.java
package com.example.health_dietcare.controller;

import com.example.health_dietcare.dto.AvatarDtos;
import com.example.health_dietcare.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/character")
public class CharacterController {
    private final CharacterService chars;

    @GetMapping("/me")
    public AvatarDtos.AvRes me(Authentication auth){
        return chars.toRes(chars.ensure(auth));
    }
}
