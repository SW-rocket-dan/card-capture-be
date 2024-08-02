package app.cardcapture.auth.jwt.config;

import java.util.Date;

public class JwtConfigStub extends JwtConfig {

    private JwtConfigStub() {
        super();
    }

    public static JwtConfig createStub() {
        return new JwtConfigStub();
    }

    @Override
    public String getIssuer() {
        return "test-issuer";
    }

    @Override
    public String getSecret() {
        return "test-secret";
    }

    @Override
    public Long getAccessExpirationInSeconds() {
        return 3600L; // 1시간
    }

    @Override
    public Date getAccessExpirationDate(Date now) {
        return new Date(now.getTime() + getAccessExpirationInSeconds() * 1000);
    }
}
