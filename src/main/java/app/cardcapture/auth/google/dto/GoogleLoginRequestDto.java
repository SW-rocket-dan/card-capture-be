package app.cardcapture.auth.google.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDto(
        @NotBlank String loginBaseUrl,
        @NotBlank String clientId,
        @NotBlank String redirectUri,
        @NotBlank String responseType,
        @NotBlank String scope,
        @NotBlank String prompt
) {
}
