package app.cardcapture.template.dto;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.template.domain.entity.Prompt;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record PromptRequestDto(
        @NonNull @Valid PhraseRequestDto phrases,
        @NotBlank @Size(max=100) String purpose,
        @NotBlank @Size(max=15) String color,
        @NotBlank AiModel model
) {
    public Prompt toEntity() {
        Prompt prompt = new Prompt();
        prompt.setPhrase(phrases.toEntity());
        prompt.setPurpose(purpose);
        prompt.setColor(color);
        prompt.setModel(model);
        return prompt;
    }
}