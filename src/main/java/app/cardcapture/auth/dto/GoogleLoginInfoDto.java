package app.cardcapture.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleLoginInfoDto {

    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
}
