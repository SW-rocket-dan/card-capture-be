package app.cardcapture.auth.jwt.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String secret;
    private Long expirationInSeconds;

    public long getExpirationMillis(long current) {
        return current + expirationInSeconds * 1000L;
    }

    public Date getExpirationDate(Date current) {
        return new Date(getExpirationMillis(current.getTime()));
    }
}
