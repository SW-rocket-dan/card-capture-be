package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.Sticker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    //@EntityGraph(attributePaths = "tags") // N+1문제 해결
    List<Sticker> findByTags_KoreanContainingOrTags_EnglishContaining(String korean, String english);
}
