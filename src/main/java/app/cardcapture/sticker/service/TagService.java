package app.cardcapture.sticker.service;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.domain.entity.StickerTag;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.sticker.repository.StickerTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final StickerTagRepository stickerTagRepository;

    public List<StickerTag> saveTags(List<TagDto> tagDtos, Sticker savedSticker) {
        return tagDtos.stream()
                .map(tagRequest -> {
                    StickerTag stickerTag = new StickerTag();
                    stickerTag.setSticker(savedSticker);
                    stickerTag.setKorean(tagRequest.getKorean());
                    stickerTag.setEnglish(tagRequest.getEnglish());
                    return stickerTagRepository.save(stickerTag);
                })
                .toList();
    }
}
