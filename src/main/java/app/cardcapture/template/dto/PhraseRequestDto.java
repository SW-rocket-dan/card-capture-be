package app.cardcapture.template.dto;

import app.cardcapture.template.domain.embed.Emphasis;
import app.cardcapture.template.domain.embed.Phrase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PhraseRequestDto(
        @NotEmpty @Size(max=10) List<@Size(max=50) String> phrases,
        @NotBlank @Size(max=50) String firstEmphasis,
        @Size(max=50) String secondEmphasis
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