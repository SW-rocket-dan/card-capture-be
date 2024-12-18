package app.cardcapture.template.dto;

import app.cardcapture.template.domain.TemplateAttribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record TemplateUpdateRequestDto(
    @Min(1) Long id,
    String editor,
    @Size(max = 50) String title,
    @Size(max = 300) String description,
    @Size(max = 500) String fileUrl,
    @Valid List<TemplateTagRequestDto> templateTags,
    @NotEmpty Set<TemplateAttribute> updatedAttributes,
    LocalDateTime createdAt
) {

}
