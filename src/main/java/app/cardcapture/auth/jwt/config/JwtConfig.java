package app.cardcapture.auth.jwt.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String secret;
    private Long accessExpirationInSeconds;
    private Long refreshExpirationInSeconds;

    public long getAccessExpirationMillis(long current) {
        return current + accessExpirationInSeconds * 1000L;
    }

    public Date getAccessExpirationDate(Date current) {
        return new Date(getAccessExpirationMillis(current.getTime()));
    }

    public LocalDateTime getAccessExpirationDate(LocalDateTime current) {
        return current.plusSeconds(accessExpirationInSeconds.intValue());
    }

    public Date getRefreshExpirationDate(Date current) {
        return new Date(current.getTime() + refreshExpirationInSeconds * 1000L);
    }
}
