package app.cardcapture.template.dto;

import app.cardcapture.template.domain.entity.Template;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TemplateEmptyResponseDto(
    @Min(1) Long templateId,
    @NotBlank String editor
){

    public static TemplateEmptyResponseDto fromEntity(Template templateEmpty) {
        return new TemplateEmptyResponseDto(
            templateEmpty.getId(),
            templateEmpty.getEditor()
        );
    }
}
