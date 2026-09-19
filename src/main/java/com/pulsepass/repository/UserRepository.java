package com.pulsepass.repository;

import com.pulsepass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // FR-USR-002
    Optional<User> findByUsername(String username);

    // NFR-007 — Query Method, busqueda case-insensitive
    Optional<User> findByEmailIgnoreCase(String email);
}
