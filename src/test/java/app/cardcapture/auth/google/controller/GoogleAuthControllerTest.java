package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.security.AuthenticatedControllerTest;
import app.cardcapture.security.EstablishedSecurityControllerTest;
import app.cardcapture.security.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoogleAuthController.class)
public class GoogleAuthControllerTest extends EstablishedSecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleAuthService googleAuthService;

    private JwtResponseDto jwtResponseDto;

    @BeforeEach
    void setUp() {
        jwtResponseDto = new JwtResponseDto("test_access_token", "test_refresh_token");
    }

    @Test
    public void getGoogleRedirect_success() throws Exception {
        // Given
        String authCode = "test_auth_code";
        given(googleAuthService.handleGoogleRedirect(authCode)).willReturn(jwtResponseDto);

        // When
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/auth/google/redirect")
                .param("code", authCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").value("test_access_token"))
            .andExpect(jsonPath("$.data.refreshToken").value("test_refresh_token"));
    }
}