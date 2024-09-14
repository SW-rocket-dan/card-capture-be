package app.cardcapture.user.service;

import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.common.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 유저id로_개인_정보를_조회할_수_있다() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        User result = userService.findUserById(1L);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(user.getEmail()),
                () -> assertThat(result.getName()).isEqualTo(user.getName())
        );
    }

    @Test
    public void 유저id로_개인_정보를_조회할_수_없으면_예외가_발생한다() {
        // given
        long invalidUserId = 1234567890L;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> {
            userService.findUserById(invalidUserId);
        });
    }

//    @Test
//    public void 유저를_생성할_수_있다() {
//        // given
//        String email = "inpink@cardcapture.app";
//        String name = "Veronica";
//        UserDto userDto = new UserDto(null, email, false, name, "GivenName", "FamilyName", "PictureUrl");
//
//        User user = User.builder()
//                .email(email)
//                .name(name)
//                .build();
//
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        // when
//        User createdUser = userService.save(userDto);
//
//        // then
//        assertAll(
//                () -> assertThat(createdUser).isNotNull(),
//                () -> assertThat(createdUser.getEmail()).isEqualTo(email),
//                () -> assertThat(createdUser.getName()).isEqualTo(name)
//        );
//    }
}
