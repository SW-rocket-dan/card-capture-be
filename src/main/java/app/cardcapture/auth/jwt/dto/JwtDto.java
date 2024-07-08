package app.cardcapture.auth.jwt.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JwtDto {
    @NonNull
    private String accessToken;
}
