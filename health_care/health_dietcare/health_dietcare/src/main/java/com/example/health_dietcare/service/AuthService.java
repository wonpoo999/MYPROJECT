package com.example.health_dietcare.service;

import com.example.health_dietcare.dto.AuthDtos;
import com.example.health_dietcare.entity.Gender;
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

    /** 회원가입 – 컨트롤러 시그니처에 맞춰 DTO로 받음 */
    public void register(AuthDtos.RegisterReq r) {
        // 기본 검증
        if (r.getUsername() == null || r.getUsername().isBlank())
            throw new IllegalArgumentException("username required");
        if (r.getPassword() == null || r.getPassword().isBlank())
            throw new IllegalArgumentException("password required");
        if (r.getPasswordCheck() != null && !r.getPassword().equals(r.getPasswordCheck()))
            throw new IllegalArgumentException("password mismatch");
        if (users.existsByUsername(r.getUsername()))
            throw new IllegalStateException("username already exists");

        // User 인스턴스 생성 (Builder 있으면 사용, 없으면 기본생성자+setter)
        User u = buildUser(r);

        // 저장
        users.save(u);
    }

    /** 로그인 – DTO로 받고 토큰과 uid 반환 */
    public AuthDtos.LoginRes login(AuthDtos.LoginReq r) {
        // 자격 검증(비밀번호 검증 포함)
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
        // 1) Builder 탐색
        try {
            Method builderMethod = User.class.getMethod("builder");
            Object builder = builderMethod.invoke(null);

            // username / password(encoding)
            callIfExists(builder, "username", String.class, r.getUsername());
            callIfExists(builder, "password", String.class, encoder.encode(r.getPassword()));

            // 선택 필드(있으면 세팅, 없으면 무시)
            callIfExists(builder, "email", String.class, r.getEmail());
            callIfExists(builder, "name", String.class, r.getName());
            callIfExists(builder, "gender", Gender.class, r.getGender());
            callIfExists(builder, "heightCm", Integer.class, r.getHeightCm());
            callIfExists(builder, "weightKg", Double.class, r.getWeightKg());
            callIfExists(builder, "publicProfile", Boolean.class, r.getPublicProfile());

            Object built = call(builder, "build");
            return (User) built;
        } catch (NoSuchMethodException ignore) {
            // 2) 기본 생성자 + setter
            try {
                User u = User.class.getDeclaredConstructor().newInstance();
                callIfExists(u, "setUsername", String.class, r.getUsername());
                callIfExists(u, "setPassword", String.class, encoder.encode(r.getPassword()));
                callIfExists(u, "setEmail", String.class, r.getEmail());
                callIfExists(u, "setName", String.class, r.getName());
                callIfExists(u, "setGender", Gender.class, r.getGender());
                callIfExists(u, "setHeightCm", Integer.class, r.getHeightCm());
                callIfExists(u, "setWeightKg", Double.class, r.getWeightKg());
                callIfExists(u, "setPublicProfile", Boolean.class, r.getPublicProfile());
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
