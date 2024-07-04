package app.cardcapture.auth.controller;

import app.cardcapture.auth.config.GoogleAuthConfig;
import app.cardcapture.auth.dto.GoogleLoginInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoogleAuthController.class)
public class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleAuthConfig googleAuthConfig;

    @Test
    public void 구글_로그인_인가_서버에_보낼_양식을_응답받을_수_있다() throws Exception {
        String clientId = "test-client-id";
        String redirectUri = "http://localhost:8080/api/v1/auth/google/callback";
        String responseType = "code";
        String scope = "openid email profile";

        given(googleAuthConfig.getClientId()).willReturn(clientId);
        given(googleAuthConfig.getRedirectUri()).willReturn(redirectUri);
        given(googleAuthConfig.getResponseType()).willReturn(responseType);
        given(googleAuthConfig.getScope()).willReturn(scope);

        mockMvc.perform(get("/api/v1/auth/google/login"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(clientId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUri").value(redirectUri))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseType").value(responseType))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scope").value(scope));
    }
}
