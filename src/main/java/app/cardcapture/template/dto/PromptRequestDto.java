package app.cardcapture.template.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record PromptRequestDto(
        @NonNull @Valid PhraseDto phrase,
        @NotBlank String purpose,
        @NotBlank String color,
        @NotBlank String model
) {}