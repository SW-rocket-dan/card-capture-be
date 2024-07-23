package app.cardcapture.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PhraseDto(
        @NotEmpty List<String> phrases,
        @NotBlank String firstEmphasis,
        String secondEmphasis
) {}