package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.TemplateTag;

public record TemplateTagRequestDto(
    String english,
    String korean
) {
    public TemplateTag toEntity() {
        TemplateTag templateTag = new TemplateTag();
        templateTag.setEnglish(english);
        templateTag.setKorean(korean);
        return templateTag;
    }
}
