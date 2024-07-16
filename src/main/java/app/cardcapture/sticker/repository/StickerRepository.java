package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    //@EntityGraph(attributePaths = "tags") // N+1문제 해결
    @Query("SELECT DISTINCT s FROM Sticker s JOIN FETCH s.tags t WHERE t.korean LIKE %:korean% OR t.english LIKE %:english%")
    List<Sticker> findByTag(String korean, String english);
}
