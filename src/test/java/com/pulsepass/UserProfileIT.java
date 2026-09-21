package com.pulsepass;

import com.pulsepass.domain.User;
import com.pulsepass.domain.UserProfile;
import com.pulsepass.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserProfileIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Debe guardar un usuario con su perfil asociado")
    void shouldSaveUserWithProfile() {
        User user = new User();
        user.setEmail("usuario@example.com");
        user.setPassword("hashedpassword123");

        UserProfile profile = new UserProfile();
        profile.setFirstName("Carlos");
        profile.setLastName("Pérez");
        profile.setPhone("+573001234567");
        profile.setUser(user);

        user.setProfile(profile);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getProfile()).isNotNull();
        assertThat(savedUser.getProfile().getFirstName()).isEqualTo("Carlos");
    }
}