package app.cardcapture.user.repository;

import app.cardcapture.user.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
