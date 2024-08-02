package app.cardcapture.sticker.service;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.dto.StickerResponseDto;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.sticker.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final StickerTagService stickerTagService;
    private final StickerRepository stickerRepository;

    public StickerResponseDto saveStickerWithTags(String fileUrl, List<TagDto> tagDtos) {
        Sticker savedSticker = saveOnlySticker(fileUrl);
        stickerTagService.saveTags(tagDtos, savedSticker);
        return getStickerResponseDto(tagDtos, savedSticker);
    }

    private Sticker saveOnlySticker(String fileUrl) {
        Sticker sticker = new Sticker();
        sticker.setFileUrl(fileUrl);
        Sticker savedSticker = stickerRepository.save(sticker);
        return savedSticker;
    }

    private StickerResponseDto getStickerResponseDto(List<TagDto> tagDtos, Sticker savedSticker) {
        return new StickerResponseDto(
                savedSticker.getId(),
                savedSticker.getFileUrl(),
                tagDtos
        );
    }

    public List<StickerResponseDto> searchStickers(String searchTerm) {
        List<Sticker> stickers = stickerRepository.findByTag(searchTerm, searchTerm);
        return stickers.stream()
                .map(sticker -> StickerResponseDto.from(sticker))
                .toList();
    }
}
