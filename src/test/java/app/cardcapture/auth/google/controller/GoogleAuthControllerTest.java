package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.config.GoogleAuthConfigStub;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.user.domain.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(GoogleAuthController.class)
@Import(GoogleAuthConfigStub.class)
public class GoogleAuthControllerTest  { // mockmvc 및 jwtComponent 등 부모 객체로 만들어서 상속

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoogleAuthConfig googleAuthConfig;

    @MockBean
    private GoogleAuthService googleAuthService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtComponent jwtComponent;

    private GoogleAuthController googleAuthController;

    @BeforeEach
    void setUp() {
        googleAuthController = new GoogleAuthController(googleAuthConfig, googleAuthService, userService, jwtComponent);
    }

    @Test
    @WithMockUser
    void testGetGoogleLoginData() throws Exception {
        mockMvc.perform(get("/api/v1/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loginBaseUrl").value("https://accounts.google.com/o/oauth2/v2/auth"))
                .andExpect(jsonPath("$.data.scope").value("profile email"))
                .andExpect(jsonPath("$.data.redirectUri").value("http://localhost:8080/api/v1/auth/google/redirect"))
                .andExpect(jsonPath("$.data.responseType").value("code"))
                .andExpect(jsonPath("$.data.clientId").value("your-client-id"));
    }

    @Test
    @WithMockUser
    void testGetGoogleRedirect() throws Exception {
        // given
        String authCode = "auth-code";
        GoogleTokenResponseDto googleTokenResponseDto = new GoogleTokenResponseDto(
                "accessToken", "refreshToken", "idToken", "tokenType", 3600);
        UserDto userDto = new UserDto("1234578910987654321", "email", true, "inpink y", "inpink", "y", "profileImageUrl");
        User user = new User(1L, "213", "email", "dsdf", true, "inpink y", "y", "profileImageUrl");
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(googleAuthService.getGoogleToken(authCode)).thenReturn(googleTokenResponseDto);
        when(googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken())).thenReturn(userDto);
        when(userService.save(userDto)).thenReturn(user);
        when(jwtComponent.create(user.getId(), "ROLE_USER")).thenReturn(jwtToken);

        // when
        mockMvc.perform(get("/api/v1/auth/google/redirect")
                        .param("code", authCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(startsWith("eyJ")));
    }
}
