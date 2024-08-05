package app.cardcapture.payment.business.repository;

import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductCategoryRepository extends JpaRepository<UserProductCategory, Long> {
    List<UserProductCategory> findByUser(User user);
}
