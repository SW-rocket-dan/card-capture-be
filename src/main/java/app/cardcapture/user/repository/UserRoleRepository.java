package app.cardcapture.user.repository;

import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByUserAndRole(User user, Role role);
}
