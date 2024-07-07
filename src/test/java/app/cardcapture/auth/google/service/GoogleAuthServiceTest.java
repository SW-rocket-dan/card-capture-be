package app.cardcapture.auth.google.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.config.GoogleAuthConfigStub;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired
    private GoogleAuthService googleAuthService;

    private GoogleAuthConfig googleAuthConfigStub;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        googleAuthConfigStub = new GoogleAuthConfigStub();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void auth_code로_구글_데이터_서버에_Token을_받아온다() throws Exception {
        // given
        GoogleTokenResponseDto mockResponse = new GoogleTokenResponseDto(
                "mock-access-token", "mock-refresh-token", "mock id token", "Bearer", 3600);

        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(googleAuthConfigStub.getOauthUrl()))
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
    public void auth_code로_구글_데이터_서버에_Token을_받아오지_못하면_예외_발생() {
        // given
        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(googleAuthConfigStub.getOauthUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> googleAuthService.getGoogleToken("mock-auth-code"))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    public void access_token으로_구글_데이터_서버에_유저정보_받아오기() throws Exception {
        // given
        String accessToken = "mock-access-token";
        UserDto mockResponse = new UserDto(
                "123456789012345678901",
                "testuser@example.com",
                true,
                "Test User",
                "Test",
                "User",
                "https://example.com/path/to/picture.jpg"
        );

        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(googleAuthConfigStub.getApiUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // when
        UserDto response = googleAuthService.getUserInfo(accessToken);

        // then
        assertAll("UserDto",
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getId()).isEqualTo("123456789012345678901"),
                () -> assertThat(response.getEmail()).isEqualTo("testuser@example.com"),
                () -> assertThat(response.isVerifiedEmail()).isTrue(),
                () -> assertThat(response.getName()).isEqualTo("Test User"),
                () -> assertThat(response.getGivenName()).isEqualTo("Test"),
                () -> assertThat(response.getFamilyName()).isEqualTo("User"),
                () -> assertThat(response.getPicture()).isEqualTo("https://example.com/path/to/picture.jpg")
        );
    }

    @Test
    public void access_token으로_구글_데이터_서버에_유저정보_못_받아오면_예외_발생() {
        // given
        String accessToken = "mock-access-token";

        mockServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(googleAuthConfigStub.getApiUrl()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> googleAuthService.getUserInfo(accessToken))
                .isInstanceOf(BusinessLogicException.class);
    }
}