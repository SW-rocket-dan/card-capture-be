package app.cardcapture.auth.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleTokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private String tokenType;
    private int expiresIn;
}
