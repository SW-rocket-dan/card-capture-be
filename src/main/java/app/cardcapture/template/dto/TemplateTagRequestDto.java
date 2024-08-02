package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.TemplateTag;
import jakarta.validation.constraints.NotBlank;

public record TemplateTagRequestDto(
    @NotBlank String english,
    @NotBlank String korean
) {
    public TemplateTag toEntity() {
        TemplateTag templateTag = new TemplateTag();
        templateTag.setEnglish(english);
        templateTag.setKorean(korean);
        return templateTag;
    }
}
