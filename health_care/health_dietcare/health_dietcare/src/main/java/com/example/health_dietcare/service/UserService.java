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

    /** /api/users/me → DTO로 변환해서 반환 */
    public UserDtos.ProfileRes me(Authentication auth) {
        String username = auth.getName();
        User u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found: " + username));

        // 엔티티에 없는 필드가 있어도 컴파일 에러 없이 null로 들어가게 안전 접근
        Long id = getOrNull(u, "getId", Long.class);
        String email = getOrNull(u, "getEmail", String.class);
        String name = getOrNull(u, "getName", String.class);
        com.example.health_dietcare.entity.Gender gender = getOrNull(u, "getGender", com.example.health_dietcare.entity.Gender.class);
        Integer height = getOrNull(u, "getHeightCm", Integer.class);
        Double weight = getOrNull(u, "getWeightKg", Double.class);
        Boolean publicProfile = getOrNull(u, "getPublicProfile", Boolean.class);
        if (publicProfile == null) publicProfile = getOrNull(u, "isPublicProfile", Boolean.class);
        com.example.health_dietcare.entity.MembershipTier tier = getOrNull(u, "getTier", com.example.health_dietcare.entity.MembershipTier.class);

        return new UserDtos.ProfileRes(
                id,
                u.getUsername(),
                email,
                name,
                gender,
                height,
                weight,
                publicProfile,
                tier
        );
    }

    /** 개인정보 공개 여부 수정 */
    public void setPrivacy(Authentication auth, UserDtos.PrivacyReq r) {
        String username = auth.getName();
        User u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found: " + username));
        Boolean open = r.getPublicProfile();
        if (open == null) return;

        // setPublicProfile(boolean) 또는 setPublicProfile(Boolean) 둘 다 대응
        if (!trySet(u, "setPublicProfile", boolean.class, open.booleanValue())) {
            trySet(u, "setPublicProfile", Boolean.class, open);
        }
        users.save(u);
    }

    // =============== reflection helpers ===============

    @SuppressWarnings("unchecked")
    private static <T> T getOrNull(Object target, String method, Class<T> type) {
        try {
            var m = target.getClass().getMethod(method);
            Object v = m.invoke(target);
            if (v == null) return null;
            if (type.isInstance(v)) return (T) v;
        } catch (NoSuchMethodException ignore) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static boolean trySet(Object target, String method, Class<?> paramType, Object arg) {
        try {
            var m = target.getClass().getMethod(method, paramType);
            m.invoke(target, arg);
            return true;
        } catch (NoSuchMethodException ignore) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
