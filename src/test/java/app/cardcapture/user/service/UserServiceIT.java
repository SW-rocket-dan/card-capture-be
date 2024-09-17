package app.cardcapture.user.service;

import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class UserServiceIT {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PaymentCommonService paymentCommonService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("assignSignupReward에서 예외 발생 시 트랜잭션 롤백 확인")
    public void testSaveUserRollbackOnException() {
        // given
        User user = new User();
        user.setGoogleId("1234567890");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(Role.USER);

        user.setRoles(Set.of(userRole));

        doThrow(new RuntimeException("Intentional RuntimeException"))
            .when(paymentCommonService)
            .saveUserProductCategory(any(), anyInt(), eq(user));

        // when && then
        assertThrows(RuntimeException.class, () -> userService.save(user));
        assertThat(userRepository.findByGoogleId("1234567890")).isEmpty();
    }
}
