package app.cardcapture.template.domain.embed;

import app.cardcapture.template.validation.ValidPhrase;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhraseDetails {

    @ElementCollection
    @ValidPhrase
    private List<String> phrases;

    @Embedded
    @Valid
    @NotNull
    private Emphasis emphasis;
}
