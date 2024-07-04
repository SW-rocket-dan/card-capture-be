package app.cardcapture.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GoogleAuthConfig {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.google.response-type}")
    private String responseType;

    @Value("${oauth.google.scope}")
    private String scope;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.base-url}")
    private String baseUrl;

    @Value("${oauth.google.oauth-url}")
    private String oauthUrl;

    @Value("${oauth.google.api-url}")
    private String apiUrl;
}
