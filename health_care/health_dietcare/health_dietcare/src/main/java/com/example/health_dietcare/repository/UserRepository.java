package com.example.health_dietcare.repository;

import com.example.health_dietcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("select (count(u) > 0) from User u where lower(u.username) = lower(:username)")
    boolean existsUsernameIgnoreCase(@Param("username") String username);

    @Query("select (count(u) > 0) from User u where lower(u.email) = lower(:email)")
    boolean existsEmailIgnoreCase(@Param("email") String email);

    @Query("select u from User u where lower(u.username) = lower(:username)")
    Optional<User> findByUsernameIgnoreCase(@Param("username") String username);
}
