package app.cardcapture.template.repository;

import app.cardcapture.template.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
