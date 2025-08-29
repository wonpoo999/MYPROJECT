package com.example.health_care.service;

import com.example.health_care.dto.AuthDtos;
import com.example.health_care.entity.User;
import com.example.health_care.repository.UserRepository;
import com.example.health_care.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    @Transactional
    public void register(AuthDtos.RegisterReq r){
        if (users.existsByUsername(r.getUsername())) throw new IllegalArgumentException("USERNAME_EXISTS");
        if (users.existsByEmail(r.getEmail())) throw new IllegalArgumentException("EMAIL_EXISTS");
        if (!r.getPassword().equals(r.getPasswordCheck())) throw new IllegalArgumentException("PW_MISMATCH");
        if (!r.getEmail().equalsIgnoreCase(r.getEmailCheck())) throw new IllegalArgumentException("EMAIL_MISMATCH");

        User u = User.builder()
                .username(r.getUsername())
                .password(encoder.encode(r.getPassword()))
                .email(r.getEmail())
                .name(r.getName())
                .gender(r.getGender())
                .heightCm(r.getHeightCm())
                .weightKg(r.getWeightKg())
                .bornTown(r.getBornTown())
                .livedTown(r.getLivedTown())
                .motherName(r.getMotherName())
                .dogName(r.getDogName())
                .elementary(r.getElementary())
                .publicProfile(Boolean.TRUE.equals(r.getPublicProfile()))
                .build();
        users.save(u);
    }

    public AuthDtos.LoginRes login(AuthDtos.LoginReq r){
        User u = users.findByUsername(r.getUsername()).orElseThrow(() -> new IllegalArgumentException("NO_USER"));
        if (!encoder.matches(r.getPassword(), u.getPassword())) throw new IllegalArgumentException("BAD_CREDENTIALS");
        String token = jwt.createAccess(u.getUsername(), u.getRole(), u.getId());
        return new AuthDtos.LoginRes(token, u.getId());
    }
}
