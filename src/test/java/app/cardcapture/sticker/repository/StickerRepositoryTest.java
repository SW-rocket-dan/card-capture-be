package app.cardcapture.sticker.repository;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.domain.entity.StickerTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.stream.Stream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class StickerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private StickerTagRepository stickerTagRepository;

    @BeforeEach
    public void setUp() {
        saveStickersAndTags();
    } // 영속성 컨텍스트를 안거치고 바로 DB에서 가져오는 방법 조사하고 적용하고 말씀드리기

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveStickersAndTags() {
        Sticker sticker1 = new Sticker();
        sticker1.setFileUrl("testUrl1");

        Sticker sticker2 = new Sticker();
        sticker2.setFileUrl("testUrl2");

        entityManager.persistAndFlush(sticker1);
        entityManager.persistAndFlush(sticker2);

        StickerTag koreanStickerTag = new StickerTag();
        koreanStickerTag.setSticker(sticker1);
        koreanStickerTag.setKorean("안녕하세요");
        koreanStickerTag.setEnglish("hello");

        StickerTag englishStickerTag = new StickerTag();
        englishStickerTag.setSticker(sticker2);
        englishStickerTag.setKorean("안녕");
        englishStickerTag.setEnglish("hi");

        entityManager.persistAndFlush(koreanStickerTag);
        entityManager.persistAndFlush(englishStickerTag);

        entityManager.clear();
    }

    @ParameterizedTest
    @MethodSource("provideTagKeywords")
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void 스티커를_태그로_조회할_수_있다(String koreanKeyword, String englishKeyword, int expectedSize, String[] expectedUrls) {
        // when
        List<Sticker> result = stickerRepository.findByTag(koreanKeyword, englishKeyword);

        // then
        assertAll(
                () -> assertThat(result).hasSize(expectedSize),
                () -> assertThat(result).extracting("fileUrl").containsExactlyInAnyOrder(expectedUrls)
        );
    }

    private static Stream<Arguments> provideTagKeywords() {
        return Stream.of(
                Arguments.of("안녕", "hello", 2, new String[]{"testUrl1", "testUrl2"}),
                Arguments.of("안녕하세요", "hi", 2, new String[]{"testUrl1", "testUrl2"}),
                Arguments.of("안녕", "kk", 2, new String[]{"testUrl1", "testUrl2"}),
                Arguments.of("안녕하세요", "hello", 1, new String[]{"testUrl1"}),
                Arguments.of("헿", "kk", 0, new String[]{}),
                Arguments.of("없는", "letter", 0, new String[]{})
        );
    }
}
