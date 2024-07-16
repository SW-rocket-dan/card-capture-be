package app.cardcapture.sticker.dto;

import app.cardcapture.sticker.domain.Sticker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StickerResponseDto {
    private Long id;
    private String fileUrl;
    private List<TagDto> tags;

    public static StickerResponseDto from(Sticker sticker) {
        return new StickerResponseDto(
                sticker.getId(),
                sticker.getFileUrl(),
                sticker.getTags().stream()
                        .map(tag -> TagDto.from(tag))
                        .toList()
        );
    }
}
