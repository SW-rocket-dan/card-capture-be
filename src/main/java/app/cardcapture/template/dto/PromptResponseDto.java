package app.cardcapture.template.dto;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.template.domain.entity.Prompt;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record PromptResponseDto(
    @NonNull PhraseDetailsResponseDto phraseDetails,
    @NotBlank String purpose,
    @NotBlank String color,
    @NotBlank AiModel model
) {

    public static PromptResponseDto fromEntity(Prompt prompt) {
        return new PromptResponseDto(
            PhraseDetailsResponseDto.fromEntity(prompt.getPhraseDetails()),
            prompt.getPurpose(),
            prompt.getColor(),
            prompt.getModel()
        );
    }
}
