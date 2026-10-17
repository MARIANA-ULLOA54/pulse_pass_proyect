package com.pulsepass.repository;

import com.pulsepass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByUsernameAndId(String username, Long id);
    boolean existsByEmail(String email);
    boolean existsByEmailAndId(String email, Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
