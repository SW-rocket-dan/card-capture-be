package app.cardcapture.sticker.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StickerSaveRequestDto {
    private String fileUrl;
    private List<TagDto> tags;
}
