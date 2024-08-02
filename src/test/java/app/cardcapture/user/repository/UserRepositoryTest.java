package app.cardcapture.user.repository;

import app.cardcapture.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback(true)
    public void 유저id로_개인_정보를_조회할_수_있다() {
        // given
        User user = User.builder()
                .googleId("1234578910987654321")
                .email("inpink@cardcapture.app")
                .name("Veronica y")
                .familyName("y")
                .givenName("Veronica")
                .build();

        entityManager.persistAndFlush(user);

        // when
        Optional<User> result = userRepository.findById(user.getId());

        // then
        assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().getEmail()).isEqualTo(user.getEmail()),
                () -> assertThat(result.get().getName()).isEqualTo(user.getName())
        );
    }
}
