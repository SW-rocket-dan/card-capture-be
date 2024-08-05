package app.cardcapture.payment.business.repository;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductCategoryRepository extends JpaRepository<UserProductCategory, Long> {
    List<UserProductCategory> findByUser(User user);

    boolean existsByUserAndProductCategory(User user, ProductCategory productCategory);

    Optional<UserProductCategory> findByUserAndProductCategory(User user, ProductCategory productCategory);
}
