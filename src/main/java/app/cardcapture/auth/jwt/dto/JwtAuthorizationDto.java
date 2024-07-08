package app.cardcapture.auth.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtAuthorizationDto {
    @NotBlank
    private String aceessToken;
}