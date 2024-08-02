package app.cardcapture.template.dto;

import app.cardcapture.template.domain.embed.Emphasis;
import app.cardcapture.template.domain.embed.Phrase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PhraseRequestDto(
        @NotEmpty List<String> phrases,
        @NotBlank String firstEmphasis,
        String secondEmphasis
) {
    public Phrase toEntity() {
        Phrase phrase = new Phrase();
        Emphasis emphasis = getEmphasis();

        phrase.setPhrases(phrases);
        phrase.setEmphasis(emphasis);

        return phrase;
    }

    private Emphasis getEmphasis() {
        Emphasis emphasis = new Emphasis();
        emphasis.setFirstEmphasis(firstEmphasis);
        emphasis.setSecondEmphasis(secondEmphasis);
        return emphasis;
    }
}