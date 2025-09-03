// src/main/java/com/example/health_dietcare/service/AuthService.java
package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.AuthDtos;
import com.example.health_dietcare.entity.User;
import com.example.health_dietcare.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-exp-min:180}")
    private long accessExpMin;

    /** 회원가입 */
    public void register(AuthDtos.RegisterReq r) {
        if (r.getUsername() == null || r.getUsername().isBlank())
            throw new IllegalArgumentException("username required");
        if (r.getPassword() == null || r.getPassword().isBlank())
            throw new IllegalArgumentException("password required");
        if (r.getPasswordCheck() != null && !r.getPassword().equals(r.getPasswordCheck()))
            throw new IllegalArgumentException("password mismatch");
        if (r.getEmail() == null || r.getEmail().isBlank())
            throw new IllegalArgumentException("email required");
        if (r.getEmailCheck() != null && !r.getEmail().equals(r.getEmailCheck()))
            throw new IllegalArgumentException("email mismatch");

        if (users.existsUsernameIgnoreCase(r.getUsername()) || users.existsByUsername(r.getUsername()))
            throw new IllegalStateException("username already exists");
        if (users.existsEmailIgnoreCase(r.getEmail()) || users.existsByEmail(r.getEmail()))
            throw new IllegalStateException("email already exists");

        // ✅ 범위 검증
        if (r.getHeightCm() != null) {
            int h = r.getHeightCm();
            if (h < 100 || h > 400) throw new IllegalArgumentException("존재하지 않는 신장");
        }
        if (r.getWeightKg() != null) {
            double w = r.getWeightKg();
            if (w < 15 || w > 500) throw new IllegalArgumentException("존재하지 않는 체중");
        }
        if (r.getAge() == null) {
            throw new IllegalArgumentException("나이를 입력하세요.");
        } else {
            int a = r.getAge();
            if (a < 10 || a > 120) throw new IllegalArgumentException("유효하지 않은 나이");
        }

        User u = buildUser(r);
        users.save(u);
    }

    /** 로그인 – 토큰과 uid 반환 */
    public AuthDtos.LoginRes login(AuthDtos.LoginReq r) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(r.getUsername(), r.getPassword())
        );
        User u = users.findByUsername(r.getUsername())
                .orElseThrow(() -> new IllegalStateException("user not found"));
        String token = issueJwt(u.getUsername(), u.getId());
        return new AuthDtos.LoginRes(token, u.getId());
    }

    // ========================= 내부 유틸 =========================

    /** DTO → User 빌드(Builder가 있으면 활용, 아니면 setter로 세팅) */
    private User buildUser(AuthDtos.RegisterReq r) {
        try {
            // 1) Builder 우선
            Method builderMethod = User.class.getMethod("builder");
            Object b = builderMethod.invoke(null);

            callIfExists(b, "username", String.class, r.getUsername());
            callIfExists(b, "password", String.class, encoder.encode(r.getPassword()));
            callIfExists(b, "email",    String.class, r.getEmail());
            callIfExists(b, "name",     String.class, r.getName());
            callIfExists(b, "gender",   User.Gender.class, r.getGender());
            callIfExists(b, "heightCm", Integer.class, r.getHeightCm());
            callIfExists(b, "weightKg", Double.class,  r.getWeightKg());
            callIfExists(b, "publicProfile", Boolean.class, r.getPublicProfile());

            // ✅ 추가 매핑: 나이
            callIfExists(b, "age", Integer.class, r.getAge());

            callIfExists(b, "bornTown",   String.class, r.getBornTown());
            callIfExists(b, "livedTown",  String.class, r.getLivedTown());
            callIfExists(b, "motherName", String.class, r.getMotherName());
            callIfExists(b, "dogName",    String.class, r.getDogName());
            callIfExists(b, "elementary", String.class, r.getElementary());

            Object built = call(b, "build");
            return (User) built;

        } catch (NoSuchMethodException ignore) {
            // 2) 기본 생성자 + setter
            try {
                User u = User.class.getDeclaredConstructor().newInstance();
                callIfExists(u, "setUsername", String.class, r.getUsername());
                callIfExists(u, "setPassword", String.class, encoder.encode(r.getPassword()));
                callIfExists(u, "setEmail",    String.class, r.getEmail());
                callIfExists(u, "setName",     String.class, r.getName());
                callIfExists(u, "setGender",   User.Gender.class, r.getGender());
                callIfExists(u, "setHeightCm", Integer.class, r.getHeightCm());
                callIfExists(u, "setWeightKg", Double.class,  r.getWeightKg());
                callIfExists(u, "setPublicProfile", Boolean.class, r.getPublicProfile());

                // ✅ 추가 매핑: 나이
                callIfExists(u, "setAge", Integer.class, r.getAge());

                callIfExists(u, "setBornTown",   String.class, r.getBornTown());
                callIfExists(u, "setLivedTown",  String.class, r.getLivedTown());
                callIfExists(u, "setMotherName", String.class, r.getMotherName());
                callIfExists(u, "setDogName",    String.class, r.getDogName());
                callIfExists(u, "setElementary", String.class, r.getElementary());
                return u;
            } catch (Exception e) {
                throw new RuntimeException("Cannot construct User", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot construct User via builder", e);
        }
    }

    /** JWT 발급 (jjwt 0.11.x, HS256) */
    private String issueJwt(String username, Long uid) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(accessExpMin));
        byte[] keyBytes = Base64.getEncoder().encode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(username)
                .claim("uid", Optional.ofNullable(uid).orElse(-1L))
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // -------- reflection helpers --------
    private static void callIfExists(Object target, String method, Class<?> paramType, Object arg) {
        if (arg == null) return;
        try {
            Method m = target.getClass().getMethod(method, paramType);
            m.invoke(target, arg);
        } catch (NoSuchMethodException ignore) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static Object call(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method);
        return m.invoke(target);
    }
}
