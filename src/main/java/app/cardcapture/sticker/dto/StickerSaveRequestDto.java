package app.cardcapture.sticker.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class StickerSaveRequestDto {
    @Size(max=300) private String fileUrl;
    private List<TagDto> tags;
}
