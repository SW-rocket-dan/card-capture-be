package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.domain.entity.StickerTag;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class StickerTagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StickerTagRepository stickerTagRepository;

    @Test
    @Rollback(true)
    public void 태그를_저장할_때_sticker_id가_없으면_예외_발생한다() {
        // given
        StickerTag stickerTagWithoutSticker = new StickerTag();
        stickerTagWithoutSticker.setKorean("안녕하세요");
        stickerTagWithoutSticker.setEnglish("hello");

        // when & then
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(stickerTagWithoutSticker);
        });
    }

    @Test
    @Rollback(true)
    public void 태그를_저장할_수_있다() {
        // given
        Sticker sticker = new Sticker();
        sticker.setFileUrl("testUrl");

        entityManager.persistAndFlush(sticker);

        StickerTag stickerTag = new StickerTag();
        stickerTag.setSticker(sticker);
        stickerTag.setKorean("안녕하세요");
        stickerTag.setEnglish("hello");

        // when
        stickerTagRepository.save(stickerTag);
        StickerTag savedStickerTag = stickerTagRepository.findById(stickerTag.getId()).orElse(null);

        // then
        assertAll(
                () -> assertThat(savedStickerTag).isNotNull(),
                () -> assertThat(savedStickerTag.getSticker()).isEqualTo(sticker),
                () -> assertThat(savedStickerTag.getKorean()).isEqualTo("안녕하세요"),
                () -> assertThat(savedStickerTag.getEnglish()).isEqualTo("hello")
        );
    }
}