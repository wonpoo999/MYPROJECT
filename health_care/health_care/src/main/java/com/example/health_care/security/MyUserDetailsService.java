package com.example.health_care.security;

import com.example.health_care.entity.User;
import com.example.health_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())));
    }
}
