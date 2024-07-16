package app.cardcapture.sticker.dto;

import app.cardcapture.sticker.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    private String korean;
    private String english;

    public static TagDto from(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.korean = tag.getKorean();
        tagDto.english = tag.getEnglish();
        return tagDto;
    }
}
