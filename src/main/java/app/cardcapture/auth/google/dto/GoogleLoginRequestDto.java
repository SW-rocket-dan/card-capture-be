package app.cardcapture.auth.google.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GoogleLoginRequestDto {

    @NonNull
    private String loginBaseUrl;

    @NonNull
    private String clientId;

    @NonNull
    private String redirectUri;

    @NonNull
    private String responseType;

    @NonNull
    private String scope;
}
