package com.example.health_dietcare.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret:}")
    private String secret; // 비어있어도 구동되도록 내성 처리

    @Value("${app.jwt.access-exp-min:180}")
    private long accessExpMin;

    private SecretKey key;

    @PostConstruct
    public void initKey() {
        byte[] raw = decodeSecretSafe(secret);

        // HS256은 32바이트(256bit) 이상 필요. 짧으면 SHA-256으로 32바이트 파생해서 사용 (개발 내성)
        if (raw.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                raw = md.digest(raw.length == 0 ? "default-dev-secret".getBytes(StandardCharsets.UTF_8) : raw);
                log.warn("[JWT] Provided secret was empty/short; derived a 32-byte key via SHA-256 (dev/local fallback).");
            } catch (Exception e) {
                throw new IllegalStateException("Cannot derive key from short/empty JWT secret", e);
            }
        }

        this.key = Keys.hmacShaKeyFor(raw);
    }

    /** 접두사 지원 + 자동 판별 (plain:, base64:, base64url:) */
    private static byte[] decodeSecretSafe(String s) {
        String v = (s == null) ? "" : s.trim();
        if (v.startsWith("plain:")) {
            return v.substring(6).getBytes(StandardCharsets.UTF_8);
        }
        if (v.startsWith("base64:")) {
            try { return Decoders.BASE64.decode(v.substring(7)); } catch (RuntimeException ignore) { /* fallthrough */ }
        }
        if (v.startsWith("base64url:")) {
            try { return Decoders.BASE64URL.decode(v.substring(10)); } catch (RuntimeException ignore) { /* fallthrough */ }
        }

        // 접두사 없으면: URL-SAFE → 표준 Base64 → 평문 순서로 시도
        try { return Decoders.BASE64URL.decode(v); } catch (RuntimeException ignore) { }
        try { return Decoders.BASE64.decode(v); } catch (RuntimeException ignore) { }
        return v.getBytes(StandardCharsets.UTF_8);
    }

    public String generate(String username, String role, Long uid) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessExpMin * 60);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("uid", uid)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }
}
