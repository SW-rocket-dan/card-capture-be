package app.cardcapture.template.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TemplateEditorUpdateRequestDto(
        @Min(1) Long id,
        @NotBlank String editor
) {
}
