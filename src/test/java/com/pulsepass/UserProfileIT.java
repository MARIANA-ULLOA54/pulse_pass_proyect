package com.pulsepass;

import com.pulsepass.domain.User;
import com.pulsepass.domain.UserProfile;
import com.pulsepass.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserProfileIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Debe guardar un usuario con su perfil asociado")
    void shouldSaveUserWithProfile() {
        User user = new User();
        user.setUsername("carlos");
        user.setEmail("usuario@example.com");

        UserProfile profile = new UserProfile();
        profile.setFirstName("Carlos");
        profile.setLastName("Pérez");
        profile.setPhone("+573001234567");
        profile.setBirthDate(LocalDate.of(1995, 3, 1));
        profile.setUser(user);
        user.setProfile(profile);

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProfile()).isNotNull();
        assertThat(saved.getProfile().getFirstName()).isEqualTo("Carlos");
    }
}
