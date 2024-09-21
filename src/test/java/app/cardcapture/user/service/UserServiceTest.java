package app.cardcapture.user.service;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.NewUserProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private PaymentCommonService paymentCommonService;

    @Mock
    private UserProductCategoryRepository userProductCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

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

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

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

        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> userService.findUserById(invalidUserId));
    }

    @Test
    public void 유저를_생성할_수_있다() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        NewUserProductCategory newUserProductCategory = NewUserProductCategory.FREE_AI_POSTER_PRODUCTION_TICKET;

        given(userRepository.save(user)).willReturn(user);
        doNothing().when(paymentCommonService).saveUserProductCategory(
            newUserProductCategory.getProductCategory(),
            newUserProductCategory.getCount(),
            user
        );

        // when
        User result = userService.save(user);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
        verify(paymentCommonService).saveUserProductCategory(
            newUserProductCategory.getProductCategory(),
            newUserProductCategory.getCount(),
            user
        );
    }

    @Test
    public void 중복된_유저_저장시_DUPLICATED_USER_예외가_발생한다() {
        // given
        User existingUser = new User();
        existingUser.setGoogleId("existingGoogleId");

        User newUser = new User();
        newUser.setGoogleId("existingGoogleId");

        given(userRepository.findByGoogleId("existingGoogleId"))
            .willReturn(Optional.of(existingUser));

        // when & then
        BusinessLogicException exception = assertThrows(
            BusinessLogicException.class,
            () -> userService.save(newUser)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_USER);
    }

    @Test
    public void 중복된_유저_역할_저장시_DUPLICATED_USER_예외가_발생한다() {
        // given
        User user = new User();
        user.setGoogleId("newGoogleId");

        UserRole existingUserRole = new UserRole();
        existingUserRole.setUser(user);
        existingUserRole.setRole(Role.USER);

        given(userRoleRepository.findByUserAndRole(user, Role.USER))
            .willReturn(Optional.of(existingUserRole));

        // when & then
        BusinessLogicException exception = assertThrows(
            BusinessLogicException.class,
            () -> userService.save(user)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_USER);
    }

    @Test
    public void googleId로_유저가_존재하는지_확인할_수_있다() {
        // given
        String googleId = "test-google-id";
        given(userRepository.existsByGoogleId(googleId)).willReturn(true);

        // when
        boolean result = userService.existsByGoogleId(googleId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void googleId로_유저_정보를_조회할_수_있다() {
        // given
        String googleId = "test-google-id";
        User user = new User();
        user.setGoogleId(googleId);
        user.setEmail("inpink@cardcapture.app");

        given(userRepository.findByGoogleId(googleId)).willReturn(Optional.of(user));

        // when
        User result = userService.findByGoogleId(googleId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGoogleId()).isEqualTo(googleId);
    }

    @Test
    public void googleId로_유저_정보를_조회할_수_없으면_예외가_발생한다() {
        // given
        String googleId = "invalid-google-id";
        given(userRepository.findByGoogleId(googleId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> userService.findByGoogleId(googleId));
    }

    @Test
    public void 유저의_상품_카테고리를_조회할_수_있다() {
        // given
        User user = new User();
        user.setId(1L);
        List<UserProductCategory> categories = List.of(new UserProductCategory());

        given(userProductCategoryRepository.findByUser(user)).willReturn(categories);

        // when
        List<UserProductCategory> result = userService.getUserProductCategories(user);

        // then
        assertThat(result).isEqualTo(categories);
    }

}