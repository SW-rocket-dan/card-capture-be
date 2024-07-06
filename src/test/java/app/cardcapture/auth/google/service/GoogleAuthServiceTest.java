package app.cardcapture.auth.google.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.common.exception.BusinessLogicException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class GoogleAuthServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private GoogleAuthConfig googleAuthConfig;

    @Autowired
    private GoogleAuthService googleAuthService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        when(googleAuthConfig.getOauthUrl()).thenReturn("https://oauth2.googleapis.com/token");
        when(googleAuthConfig.getClientId()).thenReturn("your-client-id");
        when(googleAuthConfig.getClientSecret()).thenReturn("your-client-secret");
        when(googleAuthConfig.getRedirectUri()).thenReturn("your-redirect-uri");
        when(googleAuthConfig.getGrantType()).thenReturn("authorization_code");
    }

    @Test
    public void testGetGoogleToken_Success() throws Exception {
        // given
        GoogleTokenResponseDto mockResponse = new GoogleTokenResponseDto(
                "mock-access-token", "mock-refresh-token", "mock id token", "Bearer", 3600);

        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // when
        GoogleTokenResponseDto response = googleAuthService.getGoogleToken("mock-auth-code");

        // then
        assertAll("GoogleTokenResponseDto",
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getAccessToken()).isEqualTo("mock-access-token"),
                () -> assertThat(response.getTokenType()).isEqualTo("Bearer"),
                () -> assertThat(response.getExpiresIn()).isEqualTo(3600),
                () -> assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token")
        );
    }

    @Test
    public void testGetGoogleToken_Failure() {
        // given
        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            googleAuthService.getGoogleToken("mock-auth-code");
        });

        // then
        assertAll("BusinessLogicException",
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception).isInstanceOf(BusinessLogicException.class)
        );
    }
}