package app.cardcapture.template.dto;

import app.cardcapture.template.domain.embed.Phrase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PhraseResponseDto(
        @NotEmpty List<String> phrases,
        @NotBlank String firstEmphasis,
        String secondEmphasis
) {
    public static PhraseResponseDto fromEntity(Phrase phrase) {
        return new PhraseResponseDto(
                phrase.getPhrases(),
                phrase.getEmphasis().getFirstEmphasis(),
                phrase.getEmphasis().getSecondEmphasis()
        );
    }
}
