package app.cardcapture.ai.openai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AiImageChangeReqeustDto(
    @Min(1) Long aiImageId,
    @NotBlank String message
){

}
