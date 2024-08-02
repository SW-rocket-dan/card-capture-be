package app.cardcapture.auth.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtAuthorizationDto(
        @NotBlank String aceessToken
) {
}
