package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.config.GoogleAuthConfigStub;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.auth.jwt.service.JwtComponentStub;
import app.cardcapture.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import static org.mockito.Mockito.mock;

public class GoogleAuthControllerStub extends GoogleAuthController {

    private GoogleAuthControllerStub(GoogleAuthConfig googleAuthConfig, GoogleAuthService googleAuthService, JwtComponent jwtComponent) {
        super(googleAuthConfig, googleAuthService, jwtComponent);
    }

    public static GoogleAuthControllerStub createStub() {
        GoogleAuthConfig googleAuthConfig = GoogleAuthConfigStub.createStub();
        GoogleAuthService googleAuthService = mock(GoogleAuthService.class);
        JwtComponent jwtComponent = JwtComponentStub.createStub();

        return new GoogleAuthControllerStub(googleAuthConfig, googleAuthService, jwtComponent);
    }

    public static GoogleAuthControllerStub createStub(GoogleAuthService googleAuthService) {
        GoogleAuthConfig googleAuthConfig = GoogleAuthConfigStub.createStub();
        JwtComponent jwtComponent = JwtComponentStub.createStub();

        return new GoogleAuthControllerStub(googleAuthConfig, googleAuthService, jwtComponent);
    }

    @Override
    public ResponseEntity<GoogleLoginRequestDto> getGoogleLoginData() {
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .loginBaseUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .scope("profile email")
                .redirectUri("http://localhost:8080/api/v1/auth/google/redirect")
                .responseType("code")
                .clientId("your-client-id")
                .build();

        return ResponseEntity.ok(googleLoginRequestDto);
    }

    @Override
    public ResponseEntity<JwtDto> getGoogleRedirect(@RequestParam(name = "code") String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = new GoogleTokenResponseDto(
                "accessToken", "refreshToken", "idToken", "tokenType", 3600);
        UserDto userDto = new UserDto(12345789L, "email", true, "inpink y", "inpink", "y", "profileImageUrl");

        String jwt = "eyJ.mock.jwt.token";
        JwtDto jwtDto = new JwtDto(jwt);

        return ResponseEntity.ok(jwtDto);
    }
}