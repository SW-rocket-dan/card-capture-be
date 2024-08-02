package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.TemplateTag;
import jakarta.validation.constraints.NotBlank;

public record TemplateTagResponseDto(
        @NotBlank String english,
        @NotBlank String korean
) {
    public static TemplateTagResponseDto fromEntity(TemplateTag templateTag) {
        return new TemplateTagResponseDto(
                templateTag.getEnglish(),
                templateTag.getKorean()
        );
    }
}
