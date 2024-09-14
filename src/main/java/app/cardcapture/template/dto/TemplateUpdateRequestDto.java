package app.cardcapture.template.dto;

import app.cardcapture.template.domain.TemplateAttribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record TemplateUpdateRequestDto(
    @Min(1) Long id,
    String editor, // TODO: 길이 제한, format 확인
    @Size(max = 50) String title,
    @Size(max = 300) String description,
    @Size(max = 500) String fileUrl,
    @Valid List<TemplateTagRequestDto> templateTags,
    @NotEmpty Set<TemplateAttribute> updatedAttributes
) {

}
