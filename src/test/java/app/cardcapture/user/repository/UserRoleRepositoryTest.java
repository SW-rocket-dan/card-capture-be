package app.cardcapture.user.repository;


import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
public class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUserAndRole_유저와_역할로_조회시_정확한_결과를_반환한다() {
        // given
        User user = new User();
        user.setGoogleId("1234578910987654321");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");
        userRepository.save(user);

        UserRole userRole = new UserRole(user, Role.USER, null, null);
        userRoleRepository.save(userRole);

        // when
        Optional<UserRole> result = userRoleRepository.findByUserAndRole(user, Role.USER);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(user);
        assertThat(result.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    public void findByUserAndRole_유저와_역할이_존재하지_않을_경우_빈값을_반환한다() {
        // given
        User user = new User();
        user.setGoogleId("1234578910987654321");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");
        userRepository.save(user);

        // when
        Optional<UserRole> result = userRoleRepository.findByUserAndRole(user, Role.ADMIN);

        // then
        assertThat(result).isEmpty();
    }
}
