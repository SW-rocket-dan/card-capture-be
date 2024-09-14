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
        if (prompt == null) {
            return null;
        } //TODO: 수정하면서 싹 갈아엎어야 함

        return new PromptResponseDto(
            PhraseDetailsResponseDto.fromEntity(prompt.getPhraseDetails()),
            prompt.getPurpose(),
            prompt.getColor(),
            prompt.getModel()
        );
    }
}
