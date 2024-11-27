package app.cardcapture.template.repository;

import app.cardcapture.template.domain.entity.Template;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("SELECT t FROM Template t " +
        "LEFT JOIN FETCH t.templateTags " +
        "LEFT JOIN FETCH t.prompt " +
        "WHERE t.user.id = :userId")
    List<Template> findByUserIdWithRelations(@Param("userId") Long userId);  // TODO: N+1 문제 해결해야함
}
