package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.AvatarDtos;
import com.example.health_dietcare.entity.Avatar;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.AvatarRepository;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatars;
    private final UserRepository users;

    /** 아바타 생성 또는 조회 → AvRes */
    @Transactional
    public AvatarDtos.AvRes createOrGet(Authentication auth, AvatarDtos.CreateReq r) {
        String username = auth.getName();
        User me = users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found: " + username));

        Optional<Avatar> opt = avatars.findByUser(me);

        Avatar av = opt.orElseGet(() -> avatars.save(
                Avatar.builder()
                        .user(me)
                        .nickname((r != null && r.getNickname() != null && !r.getNickname().isBlank())
                                ? r.getNickname() : me.getUsername())
                        .level(1).hp(100).atk(10).def(5).exp(0)
                        .build()
        ));

        if (r != null && r.getNickname() != null && !r.getNickname().isBlank()
                && !r.getNickname().equals(av.getNickname())) {
            av.setNickname(r.getNickname());
            av = avatars.save(av);
        }

        return AvatarDtos.of(av);
    }
}
