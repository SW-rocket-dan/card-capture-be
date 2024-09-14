package app.cardcapture.template.dto;

import app.cardcapture.template.domain.embed.Emphasis;
import app.cardcapture.template.domain.embed.PhraseDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PhraseDetailsRequestDto(
        @NotEmpty @Size(max=10) List<@Size(max=100) String> phrases,
        @NotBlank @Size(max=50) String firstEmphasis,
        @Size(max=50) String secondEmphasis
) {
    public PhraseDetails toEntity() {
        PhraseDetails phraseDetails = new PhraseDetails();
        Emphasis emphasis = getEmphasis();

        phraseDetails.setPhrases(phrases);
        phraseDetails.setEmphasis(emphasis);

        return phraseDetails;
    }

    private Emphasis getEmphasis() {
        Emphasis emphasis = new Emphasis();
        emphasis.setFirstEmphasis(firstEmphasis);
        emphasis.setSecondEmphasis(secondEmphasis);
        return emphasis;
    }
}