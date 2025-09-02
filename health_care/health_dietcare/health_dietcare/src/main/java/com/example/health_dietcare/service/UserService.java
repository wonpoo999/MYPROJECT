package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.UserDtos;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;

    /** /api/users/me → 엔티티를 DTO로 매핑해서 반환 */
    public UserDtos.ProfileRes me(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("unauthenticated");
        }
        String username = auth.getName();

        User u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found: " + username));

        return UserDtos.ProfileRes.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .name(u.getName())
                .gender(u.getGender())
                .heightCm(u.getHeightCm())
                .weightKg(u.getWeightKg())
                .publicProfile(u.isPublicProfile())
                .tier(u.getTier())
                .role(u.getRole())
                .build();
    }

    /** 개인정보 공개 여부 수정 */
    public void setPrivacy(Authentication auth, UserDtos.PrivacyReq r) {
        if (auth == null || auth.getName() == null) return;
        if (r == null || r.getPublicProfile() == null) return;

        String username = auth.getName();
        User u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found: " + username));

        u.setPublicProfile(r.getPublicProfile());
        users.save(u);
    }
}
