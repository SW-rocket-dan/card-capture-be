package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.Template;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record TemplateResponseDto(
        Long id,
        Long userId,
        @NotBlank String title,
        String description,
        int likes,
        int purchaseCount,
        @NotBlank String editor,
        @NotBlank String fileUrl,
        @NotEmpty List<TemplateTagResponseDto> templateTags,
        @NotNull PromptResponseDto prompt,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) {
    public static TemplateResponseDto fromEntity(Template template) {
        return new TemplateResponseDto(
                template.getId(),
                template.getUser().getId(),
                template.getTitle(),
                template.getDescription(),
                template.getLikes(),
                template.getPurchaseCount(),
                template.getEditor(),
                template.getFileUrl(),
                template.getTemplateTags().stream().map(TemplateTagResponseDto::fromEntity).toList(),
                PromptResponseDto.fromEntity(template.getPrompt()),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}