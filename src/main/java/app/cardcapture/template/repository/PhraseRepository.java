package app.cardcapture.template.repository;

import app.cardcapture.sticker.domain.Sticker;
import app.cardcapture.template.domain.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
}
