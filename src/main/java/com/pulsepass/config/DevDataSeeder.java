package com.pulsepass.config;

import com.pulsepass.domain.User;
import com.pulsepass.domain.UserCredential;
import com.pulsepass.domain.UserRole;
import com.pulsepass.domain.enums.Role;
import com.pulsepass.repository.UserCredentialRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Crea ADMIN y STAFF de demostración. Solo con el perfil "dev"; las contraseñas salen del entorno. */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public DevDataSeeder(UserRepository userRepository, UserCredentialRepository credentialRepository,
            UserRoleRepository roleRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed("admin", "admin@pulsepass.local", Role.ADMIN, env.getProperty("PULSEPASS_DEV_ADMIN_PASSWORD"));
        seed("staff", "staff@pulsepass.local", Role.STAFF, env.getProperty("PULSEPASS_DEV_STAFF_PASSWORD"));
    }

    private void seed(String username, String email, Role role, String password) {
        if (password == null || password.length() < 12) {
            log.warn("Seed de '{}' omitido: define su contraseña (12+ caracteres) en el entorno", username);
            return;
        }
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setActive(true);
        user = userRepository.save(user);
        credentialRepository.save(new UserCredential(user.getId(), passwordEncoder.encode(password), LocalDateTime.now()));
        roleRepository.save(new UserRole(user.getId(), role));
    }
}
