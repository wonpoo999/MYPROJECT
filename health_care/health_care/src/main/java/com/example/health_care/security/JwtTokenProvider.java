package com.example.health_care.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessExpMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-exp-min:180}") long expMin) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpMs = expMin * 60_000;
    }

    public String createAccess(String username, String role, Long uid){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role, "uid", uid))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
