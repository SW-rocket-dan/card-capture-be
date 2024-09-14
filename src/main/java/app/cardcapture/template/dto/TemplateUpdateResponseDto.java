package app.cardcapture.template.dto;

import jakarta.validation.constraints.Min;

public record TemplateUpdateResponseDto(
        @Min(1) Long id
) {
}
