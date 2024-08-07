package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.List;

public record TemplateRequestDto(
        String title,
        String description,
        String fileUrl,
        List<TemplateTagRequestDto> templateTagRequestDtos,
        @NonNull @Valid PromptRequestDto promptRequestDto

) {
    public Template toEntity(Prompt prompt) {
        Template template = new Template();

        template.setTitle(title);
        template.setDescription(description);
        template.setFileUrl(fileUrl);
        template.setPrompt(prompt);

        return template;
    }
}
