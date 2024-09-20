package app.cardcapture.auth.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleTokenResponseDto(
    String accessToken,
    String refreshToken,
    String idToken,
    String tokenType,
    int expiresIn
) {

}
