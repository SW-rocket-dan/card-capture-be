package app.cardcapture.user.service;

import app.cardcapture.user.domain.User;
import app.cardcapture.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    @Rollback(true)
    public void 유저id로_개인_정보를_조회할_수_있다() {
        // given
        User user = User.builder()
                .email("inpink@cardcapture.app")
                .name("Veronica")
                .build();
        userRepository.saveAndFlush(user);

        long userId = user.getId();

        // when
        User result = userService.findUserById(userId);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(user.getEmail()),
                () -> assertThat(result.getName()).isEqualTo(user.getName())
        );
    }

    @Test
    @Rollback(true)
    public void 유저id로_개인_정보를_조회할_수_없으면_예외가_발생한다() {
        // given
        long invalidUserId = 1234567890L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findUserById(invalidUserId);
        });
    }
}