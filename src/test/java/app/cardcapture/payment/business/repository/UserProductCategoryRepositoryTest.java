package app.cardcapture.payment.business.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.repository.UserRoleRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
@ActiveProfiles("test")
public class UserProductCategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProductCategoryRepository userProductCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "0, 0",
    })
    @Rollback(true)
    public void 이용권을_정상적으로_감소시킨다(int initialQuantity, int expectedQuantity) {
        // given
        User user = new User();
        user.setGoogleId("1234567890");
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");

        userRepository.save(user);

        UserProductCategory userProductCategory = new UserProductCategory();
        userProductCategory.setUser(user);
        userProductCategory.setProductCategory(ProductCategory.AI_POSTER_PRODUCTION_TICKET);
        userProductCategory.setQuantity(initialQuantity);
        entityManager.persistAndFlush(userProductCategory);

        // when
        userProductCategoryRepository.deductUsage(user.getId(),
            ProductCategory.AI_POSTER_PRODUCTION_TICKET);

        UserProductCategory updatedCategory = entityManager.find(UserProductCategory.class,
            userProductCategory.getId());

        // then
        assertAll(
            () -> assertThat(updatedCategory).isNotNull(),
            () -> assertThat(updatedCategory.getQuantity()).isEqualTo(expectedQuantity)
        );
    }
}
