package app.cardcapture.auth.controller;

import app.cardcapture.auth.config.GoogleAuthConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoogleAuthController.class)
public class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleAuthConfig googleAuthConfig;

    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;

    @BeforeEach
    public void setGoogleAuthConfig() {
        clientId = "test-client-id";
        redirectUri = "http://localhost:8080/api/v1/auth/google/callback";
        responseType = "code";
        scope = "openid email profile";

        given(googleAuthConfig.getClientId()).willReturn(clientId);
        given(googleAuthConfig.getRedirectUri()).willReturn(redirectUri);
        given(googleAuthConfig.getResponseType()).willReturn(responseType);
        given(googleAuthConfig.getScope()).willReturn(scope);
    }

    @Test
    public void api로_구글_로그인_인가_서버에_보낼_양식을_응답받을_수_있다() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/auth/google/login"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.redirectUri").value(redirectUri))
                .andExpect(jsonPath("$.responseType").value(responseType))
                .andExpect(jsonPath("$.scope").value(scope));
    }
}
