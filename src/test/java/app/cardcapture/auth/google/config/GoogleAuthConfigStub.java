package app.cardcapture.auth.google.config;

public class GoogleAuthConfigStub extends GoogleAuthConfig {

    private GoogleAuthConfigStub() {
        super();
    }

    public static GoogleAuthConfig createStub() {
        return new GoogleAuthConfigStub();
    }

    @Override
    public String getBaseUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth";
    }

    @Override
    public String getScope() {
        return "profile email";
    }

    @Override
    public String getRedirectUri() {
        return "http://localhost:8080/api/v1/auth/google/redirect";
    }

    @Override
    public String getResponseType() {
        return "code";
    }

    @Override
    public String getClientId() {
        return "your-client-id";
    }

    @Override
    public String getClientSecret() {
        return "your-client-secret";
    }

    @Override
    public String getOauthUrl() {
        return "https://oauth2.googleapis.com/token";
    }

    @Override
    public String getApiUrl() {
        return "https://www.googleapis.com/oauth2/v3/userinfo";
    }

    @Override
    public String getGrantType() {
        return "authorization_code";
    }
}