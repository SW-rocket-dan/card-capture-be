package app.cardcapture.auth.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtResponseDto(
        @NotBlank String accessToken,
        @NotBlank String refreshToken
) {
}
