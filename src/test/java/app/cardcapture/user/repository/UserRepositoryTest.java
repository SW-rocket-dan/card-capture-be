package app.cardcapture.user.repository;

import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    public void 유저id로_개인_정보와_역할을_조회할_수_있다() {
        // given
        User user = new User();
        user.setGoogleId("1234567890");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(Role.USER);

        user.setRoles(Set.of(userRole));

        userRepository.save(user);
        userRoleRepository.save(userRole);

        // when
        Optional<User> result = userRepository.findUserAndUserRolesByUserId(user.getId());

        // then
        assertAll(
            () -> assertThat(result).isPresent(),
            () -> assertThat(result.get().getEmail()).isEqualTo(user.getEmail()),
            () -> assertThat(result.get().getRoles()).hasSize(1),
            () -> assertThat(result.get().getRoles().iterator().next().getRole()).isEqualTo(Role.USER)
        );
    }

    @Test
    public void 구글아이디로_유저를_조회할_수_있다() {
        // given
        User user = new User();
        user.setGoogleId("1234567890");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByGoogleId(user.getGoogleId());

        // then
        assertAll(
            () -> assertThat(result).isPresent(),
            () -> assertThat(result.get().getEmail()).isEqualTo(user.getEmail())
        );
    }

    @Test
    public void 구글아이디로_유저가_존재하는지_확인할_수_있다() {
        // given
        User user = new User();
        user.setGoogleId("1234567890");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByGoogleId(user.getGoogleId());

        // then
        assertThat(exists).isTrue();
    }
}