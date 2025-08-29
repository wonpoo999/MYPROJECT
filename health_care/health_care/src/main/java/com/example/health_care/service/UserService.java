package com.example.health_care.service;

import com.example.health_care.dto.UserDtos;
import com.example.health_care.entity.User;
import com.example.health_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository users;

    public UserDtos.ProfileRes me(Authentication auth){
        String username = auth.getName();
        User u = users.findByUsername(username).orElseThrow();
        return new UserDtos.ProfileRes(u.getId(), u.getUsername(), u.getEmail(), u.getName(),
                u.getGender(), u.getHeightCm(), u.getWeightKg(), u.getPublicProfile(), u.getTier());
    }

    @Transactional
    public void setPrivacy(Authentication auth, UserDtos.PrivacyReq r){
        User u = users.findByUsername(auth.getName()).orElseThrow();
        u.setPublicProfile(Boolean.TRUE.equals(r.getPublicProfile()));
    }
}
