package app.cardcapture.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserGoogleAuthResponseDto(
    @JsonProperty("id") String googleId, // TODO: UserController이용해서 테스트코드 짜기
    String email,
    boolean verifiedEmail,
    String name,
    String givenName,
    String familyName,
    String picture,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
