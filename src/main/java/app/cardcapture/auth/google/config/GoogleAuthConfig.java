package app.cardcapture.auth.google.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleAuthConfig {
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String clientSecret;
    private String baseUrl;
    private String oauthUrl;
    private String apiUrl;
    private String grantType;
}
