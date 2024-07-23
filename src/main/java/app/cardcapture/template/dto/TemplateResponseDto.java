package app.cardcapture.template.dto;

import app.cardcapture.template.domain.Prompt;
import app.cardcapture.template.domain.TemplateTag;
import app.cardcapture.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record TemplateResponseDto(
        Long id,
        @NotNull User user,
        @NotBlank  String title,
        String description,
        int likes,
        int purchaseCount,
        @NotBlank String editor,
        @NotBlank String fileUrl,
        @NotEmpty List<TemplateTag> templateTags,
        @NotNull Prompt prompt,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) {
    public TemplateResponseDto {
        if (description == null) {
            description = "";
        }
    }
}