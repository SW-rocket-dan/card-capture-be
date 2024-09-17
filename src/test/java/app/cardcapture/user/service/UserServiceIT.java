package app.cardcapture.user.service;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.repository.UserRepository;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    @DisplayName("동시성 테스트: 중복된 유저를 동시에 저장 시도할 경우, 하나만 성공하고 나머지는 예외 발생")
    void testConcurrentUserSave() throws InterruptedException {
        // given
        // 쓰레드 개수
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        // 동시성 테스트 실행
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    User user = new User();
                    user.setGoogleId("1234567890");
                    user.setEmail("inpink@cardcapture.app");
                    user.setName("Veronica");

                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(Role.USER);

                    user.setRoles(Set.of(userRole));

                    userService.save(user); // 동시에 user 저장 시도
                    successCount.incrementAndGet(); // 성공한 경우
                } catch (BusinessLogicException e) {
                    if (e.getErrorCode() == ErrorCode.DUPLICATED_USER) {
                        failCount.incrementAndGet(); // 중복 예외 발생한 경우
                    }
                }catch (DataIntegrityViolationException e) {
                    failCount.incrementAndGet(); // 중복 예외 발생한 경우
                }
                finally {
                    latch.countDown(); // 쓰레드 작업 완료 시 latch 감소
                }
            });
        }

        latch.await(); // 모든 쓰레드가 끝날 때까지 대기
        executorService.shutdown();

        // then
        // 테스트 검증
        assertAll(
            () -> assertThat(successCount.get()).isEqualTo(1),
            () -> assertThat(failCount.get()).isEqualTo(numThreads - 1));

        // 테스트 후 데이터 삭제
        cleanUpTestData();
    }

    private void cleanUpTestData() {
        jdbcTemplate.execute("DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE google_id = '1234567890')");
        jdbcTemplate.execute("DELETE FROM users WHERE google_id = '1234567890'");
    }
}
