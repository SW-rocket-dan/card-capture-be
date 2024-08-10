package app.cardcapture.template.dto;

import app.cardcapture.template.domain.embed.PhraseDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PhraseDetailsResponseDto(
        @NotEmpty List<String> phrases,
        @NotBlank String firstEmphasis,
        String secondEmphasis
) {
    public static PhraseDetailsResponseDto fromEntity(PhraseDetails phraseDetails) {
        return new PhraseDetailsResponseDto(
                phraseDetails.getPhrases(),
                phraseDetails.getEmphasis().getFirstEmphasis(),
                phraseDetails.getEmphasis().getSecondEmphasis()
        );
    }
}
