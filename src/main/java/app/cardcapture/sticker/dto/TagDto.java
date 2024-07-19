package app.cardcapture.sticker.dto;

import app.cardcapture.sticker.domain.StickerTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    private String korean;
    private String english;

    public static TagDto from(StickerTag stickerTag) {
        TagDto tagDto = new TagDto();
        tagDto.korean = stickerTag.getKorean();
        tagDto.english = stickerTag.getEnglish();
        return tagDto;
    }
}
