package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.TemplateTag;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;

public record TemplateTagRequestDto(
        @Size(max = 15) String english,
        @Size(max = 15) String korean
) {
    public TemplateTag toEntity() {
        TemplateTag templateTag = new TemplateTag();
        templateTag.setEnglish(english);
        templateTag.setKorean(korean);
        return templateTag;
    }
}
