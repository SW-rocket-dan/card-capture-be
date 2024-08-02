package app.cardcapture.auth.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank String refreshToken
) {
}
