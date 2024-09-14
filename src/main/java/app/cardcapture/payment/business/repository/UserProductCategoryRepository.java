package app.cardcapture.payment.business.repository;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.user.domain.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductCategoryRepository extends JpaRepository<UserProductCategory, Long> {
    List<UserProductCategory> findByUser(User user);

    boolean existsByUserAndProductCategory(User user, ProductCategory productCategory);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT upc FROM UserProductCategory upc WHERE upc.user = :user AND upc.productCategory = :productCategory")
    Optional<UserProductCategory> findByUserAndProductCategoryWithLock(@Param("user") User user, @Param("productCategory") ProductCategory productCategory);
}
