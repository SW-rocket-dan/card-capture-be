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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class StickerServiceTest {

    @Mock
    private StickerTagService stickerTagService;

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

        //  BDD given when then방식 이 위치는 when이아니라 given으로 쓰면됨
        given(stickerRepository.save(any(Sticker.class))).willReturn(savedSticker);

        // when
        StickerResponseDto result = stickerService.saveStickerWithTags(fileUrl, tagDtos);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(savedSticker.getId()),
                () -> assertThat(result.getFileUrl()).isEqualTo(savedSticker.getFileUrl()),
                () -> assertThat(result.getTags()).isEqualTo(tagDtos)
        );

        // 여기도 then을쓰고 should안에 times로 몇 번 호출됐는지도 정해줄 수 있다
        then(stickerTagService).should(times(1)).saveTags(tagDtos, savedSticker);
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
} //TODO: 컨트롤러 테스트 https://velog.io/@as9587/WebMvcTest%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%9C-Controller-Test-Code%EC%97%90-Spring-Security-%EC%B6%94%EA%B0%80%ED%95%98%EA%B8%B0
