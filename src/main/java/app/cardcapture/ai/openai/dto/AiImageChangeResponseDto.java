package app.cardcapture.ai.openai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiImageChangeResponseDto(
    @Min(1) Long changedAiImageId,
    @NotBlank @Size(max = 500) String changedAiImageUrl
) {

}
