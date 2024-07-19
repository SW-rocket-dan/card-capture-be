package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.StickerTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerTagRepository extends JpaRepository<StickerTag, Long> {
}