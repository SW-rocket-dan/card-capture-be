package app.cardcapture.template.dto;

import jakarta.validation.constraints.Min;

public record TemplateEditorUpdateResponseDto(
        @Min(1) Long id
) {
}
