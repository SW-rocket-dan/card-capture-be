package app.cardcapture.sticker.service;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.dto.StickerResponseDto;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.sticker.repository.StickerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class StickerServiceTest {

    @Mock
    private TagService tagService;

    @Mock
    private StickerRepository stickerRepository;

    @InjectMocks
    private StickerService stickerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 스티커와_태그를_저장할_수_있다() {
        // given
        String fileUrl = "testUrl";
        List<TagDto> tagDtos = Arrays.asList(
                new TagDto("안녕하세요", "hello"),
                new TagDto("안녕", "hi")
        );

        Sticker sticker = new Sticker();
        sticker.setFileUrl(fileUrl);
        sticker.setStickerTags(new ArrayList<>());

        Sticker savedSticker = new Sticker();
        savedSticker.setId(1L);
        savedSticker.setFileUrl(fileUrl);
        savedSticker.setStickerTags(new ArrayList<>());

        when(stickerRepository.save(any(Sticker.class))).thenReturn(savedSticker);

        // when
        StickerResponseDto result = stickerService.saveStickerWithTags(fileUrl, tagDtos);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(savedSticker.getId()),
                () -> assertThat(result.getFileUrl()).isEqualTo(savedSticker.getFileUrl()),
                () -> assertThat(result.getTags()).isEqualTo(tagDtos)
        );
        verify(tagService).saveTags(tagDtos, savedSticker);
    }

    @Test
    public void 태그로_스티커를_검색할_수_있다() {
        // given
        String searchTerm = "안녕";
        Sticker sticker1 = new Sticker();
        sticker1.setId(1L);
        sticker1.setFileUrl("testUrl1");
        sticker1.setStickerTags(new ArrayList<>());

        Sticker sticker2 = new Sticker();
        sticker2.setId(2L);
        sticker2.setFileUrl("testUrl2");
        sticker2.setStickerTags(new ArrayList<>());

        List<Sticker> stickers = Arrays.asList(sticker1, sticker2);

        when(stickerRepository.findByTag(searchTerm, searchTerm))
                .thenReturn(stickers);

        // when
        List<StickerResponseDto> results = stickerService.searchStickers(searchTerm);

        // then
        assertAll(
                () -> assertThat(results).hasSize(2),
                () -> assertThat(results.get(0).getId()).isEqualTo(sticker1.getId()),
                () -> assertThat(results.get(0).getFileUrl()).isEqualTo(sticker1.getFileUrl()),
                () -> assertThat(results.get(1).getId()).isEqualTo(sticker2.getId()),
                () -> assertThat(results.get(1).getFileUrl()).isEqualTo(sticker2.getFileUrl())
        );
    }
}