package com.pulsepass.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsepass.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndId(String username, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndId(String email, Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
