package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfigStub;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtDto;
import app.cardcapture.auth.jwt.service.JwtComponentStub;
import app.cardcapture.user.domain.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleAuthControllerTest {

    private GoogleAuthController googleAuthController;

    private GoogleAuthService googleAuthService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        googleAuthService = mock(GoogleAuthService.class);
        userService=mock(UserService.class);
        googleAuthController = new GoogleAuthController(GoogleAuthConfigStub.createStub(), googleAuthService, userService, JwtComponentStub.createStub());
    }

    @Test
    void testGetGoogleLoginData() {
        // when
        ResponseEntity<GoogleLoginRequestDto> response = googleAuthController.getGoogleLoginData();

        // then
        GoogleLoginRequestDto body = response.getBody();
        assertAll(
                () -> assertNotNull(body),
                () -> assertEquals("https://accounts.google.com/o/oauth2/v2/auth", body.getLoginBaseUrl()),
                () -> assertEquals("profile email", body.getScope()),
                () -> assertEquals("http://localhost:8080/api/v1/auth/google/redirect", body.getRedirectUri()),
                () -> assertEquals("code", body.getResponseType()),
                () -> assertEquals("your-client-id", body.getClientId())
        );
    }

    @Test
    void testGetGoogleRedirect() {
        // given
        String authCode = "auth-code";
        GoogleTokenResponseDto googleTokenResponseDto = new GoogleTokenResponseDto(
                "accessToken", "refreshToken", "idToken", "tokenType", 3600);
        UserDto userDto = new UserDto("1234578910987654321", "email", true, "inpink y", "inpink", "y", "profileImageUrl");
        User user = new User(1L, "213", "email", "dsdf", true, "inpink y", "y", "profileImageUrl");

        when(googleAuthService.getGoogleToken(authCode)).thenReturn(googleTokenResponseDto);
        when(googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken())).thenReturn(userDto);
        when(userService.save(userDto)).thenReturn(user);

        // when
        ResponseEntity<JwtDto> response = googleAuthController.getGoogleRedirect(authCode);

        // then
        JwtDto body = response.getBody();
        assertAll(
                () -> assertNotNull(body),
                () -> assertTrue(body.getAccessToken().startsWith("eyJ"))
        );
    }
}
