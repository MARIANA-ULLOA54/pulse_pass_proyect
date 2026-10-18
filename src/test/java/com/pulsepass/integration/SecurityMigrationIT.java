package com.pulsepass.integration;

import com.pulsepass.AbstractIntegrationTest;
import com.pulsepass.domain.User;
import com.pulsepass.domain.UserRole;
import com.pulsepass.domain.enums.Role;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class SecurityMigrationIT extends AbstractIntegrationTest {

    @Autowired JdbcTemplate jdbc;
    @Autowired UserRepository userRepository;
    @Autowired UserRoleRepository roleRepository;

    private User newUser(String name) {
        User u = new User();
        u.setUsername(name);
        u.setEmail(name + "@mail.com");
        return userRepository.saveAndFlush(u);
    }

    @Test
    void v4_createsCredentialAndRoleTables() {
        Integer count = jdbc.queryForObject(
                "select count(*) from information_schema.tables "
                        + "where table_name in ('user_credentials','user_roles')", Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void invalidRole_isRejectedByConstraint() {
        User u = newUser("rol-invalido");
        assertThatThrownBy(() -> jdbc.update(
                "insert into user_roles (user_id, role) values (?, 'SUPERUSER')", u.getId()))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void duplicatedRole_isRejectedByPrimaryKey() {
        User u = newUser("rol-duplicado");
        roleRepository.saveAndFlush(new UserRole(u.getId(), Role.USER));
        assertThatThrownBy(() -> jdbc.update(
                "insert into user_roles (user_id, role) values (?, 'USER')", u.getId()))
                .isInstanceOf(DataAccessException.class);
    }
}
