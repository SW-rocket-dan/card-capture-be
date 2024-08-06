package app.cardcapture.template.dto;

import app.cardcapture.template.domain.TemplateAttribute;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public record TemplateUpdateRequestDto(
        @Min(1) Long id,
        String editor,
        String title,
        String description,
        String fileUrl,
        @NotEmpty Set<TemplateAttribute> updatedAttributes,
        List<TemplateTagRequestDto> templateTags
) {
}
