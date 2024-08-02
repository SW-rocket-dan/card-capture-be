package app.cardcapture.template.repository;

import app.cardcapture.template.domain.entity.TemplateTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateTagRepository extends JpaRepository<TemplateTag, Long> {
}
