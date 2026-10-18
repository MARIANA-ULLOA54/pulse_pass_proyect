package com.pulsepass.repository;

import com.pulsepass.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.Pk> {
    List<UserRole> findByUserId(Long userId);
}
