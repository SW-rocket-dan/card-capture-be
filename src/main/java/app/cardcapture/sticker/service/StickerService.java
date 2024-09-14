package app.cardcapture.sticker.service;

import app.cardcapture.ai.common.AiImage;
import app.cardcapture.ai.common.repository.AiImageRepository;
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
    private final AiImageRepository aiImageRepository;

    public StickerResponseDto saveStickerWithTags(
        String fileUrl,
        List<TagDto> tagDtos,
        String prompt
    ) {
        Sticker savedSticker = saveOnlySticker(fileUrl);
        stickerTagService.saveTags(tagDtos, savedSticker);

        //TODO: 분리
        AiImage aiImage = new AiImage();
        aiImage.setPrompt(prompt);
        aiImageRepository.save(aiImage);

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
