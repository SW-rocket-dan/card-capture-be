package app.cardcapture.auth.controller;


import app.cardcapture.api.LoginApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginApiController.class)
public class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 구글_로그인_인가_서버에_보낼_양식을_응답받을_수_있다() throws Exception {
        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");

        mockMvc.perform(get("/api/v1/auth/google/login"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.clientId").value(clientId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUri").value(redirectUri))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseType").value("code"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scope").value("openid email profile"));
    }


}
