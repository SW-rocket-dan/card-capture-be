package app.cardcapture.template.domain.embed;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Emphasis {

    @NotBlank
    private String firstEmphasis;

    @NotBlank
    private String secondEmphasis;
}
