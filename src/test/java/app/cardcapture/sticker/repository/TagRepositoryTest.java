package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.Sticker;
import app.cardcapture.sticker.domain.Tag;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class TagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Rollback(true)
    public void 태그를_저장할_때_sticker_id가_없으면_예외_발생한다() {
        // given
        Tag tagWithoutSticker = new Tag();
        tagWithoutSticker.setKorean("안녕하세요");
        tagWithoutSticker.setEnglish("hello");

        // when & then
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(tagWithoutSticker);
        });
    }

    @Test
    @Rollback(true)
    public void 태그를_저장할_수_있다() {
        // given

        Sticker sticker = new Sticker();
        sticker.setFileUrl("testUrl");

        entityManager.persistAndFlush(sticker);

        Tag tag = new Tag();
        tag.setSticker(sticker);
        tag.setKorean("안녕하세요");
        tag.setEnglish("hello");

        // when
        tagRepository.save(tag);
        Tag savedTag = tagRepository.findById(tag.getId()).orElse(null);

        // then
        assertAll(
                () -> assertThat(savedTag).isNotNull(),
                () -> assertThat(savedTag.getSticker()).isEqualTo(sticker),
                () -> assertThat(savedTag.getKorean()).isEqualTo("안녕하세요"),
                () -> assertThat(savedTag.getEnglish()).isEqualTo("hello")
        );
    }
}