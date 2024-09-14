package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;

public record TemplateRequestDto(
    @NonNull @Valid PromptRequestDto prompt,
    @Min(1) int count
) {

    public TemplateRequestDto {
        if (count < 1) {
            count = 1;
        }
    }

    public Template toEntity(Prompt prompt) {
        Template template = new Template();
        template.setPrompt(prompt);
        return template;
    }
}
