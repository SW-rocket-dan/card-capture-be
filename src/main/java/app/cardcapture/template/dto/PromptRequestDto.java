package app.cardcapture.template.dto;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.template.domain.entity.Prompt;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record PromptRequestDto(
        @NonNull @Valid PhraseDetailsRequestDto phraseDetails,
        @NotBlank @Size(max=100) String purpose,
        @NotBlank @Size(max=15) String color,
        @NotNull AiModel model
) {
    public Prompt toEntity() {
        Prompt prompt = new Prompt();
        prompt.setPhraseDetails(phraseDetails.toEntity());
        prompt.setPurpose(purpose);
        prompt.setColor(color);
        prompt.setModel(model);
        return prompt;
    }
}