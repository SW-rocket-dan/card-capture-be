package app.cardcapture.template.dto;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.template.domain.entity.Prompt;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record PromptRequestDto(
        @NonNull @Valid PhraseRequestDto phraseRequestDto,
        @NotBlank String purpose,
        @NotBlank String color,
        @NotBlank String model
        @NotBlank AiModel model
) {
    public Prompt toEntity() {
        Prompt prompt = new Prompt();
        prompt.setPhrase(phraseRequestDto.toEntity());
        prompt.setPurpose(purpose);
        prompt.setColor(color);
        prompt.setModel(model);
        return prompt;
    }
}