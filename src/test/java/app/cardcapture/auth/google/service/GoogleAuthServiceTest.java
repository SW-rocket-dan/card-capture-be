package app.cardcapture.auth.google.service;

import app.cardcapture.auth.google.config.GoogleAuthConfigStub;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.config.RestClientConfig;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient.Builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
//@Import({RestClientConfig.class, GoogleAuthConfigStub.class}) // TODO: GoogleAuthService를 MockBean에서 빼니까 이거 없어도 됐음
@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceTest {

//    @Autowired
    private GoogleAuthConfigStub googleAuthConfig;

//    @MockBean // TODO: 얘도 없어도 됐음. ExtendWith 달기
    @Mock
    private UserService userService;

    @MockBean
    private JwtComponent jwtComponent;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockRestServiceServer server;

    private GoogleAuthService googleAuthService; // TODO: 테스트 타겟은 직접 주입하지 말고 실제 객체 만들어 쓰기. 1. 우리가 의존성을 직접 관리해야 우리가 의도한대로 테스트환경을 구성할 수 있음. 이 service autowired빼기

    @Autowired
    private Builder restClientBuilder;

    @BeforeEach
    public void setUp() {
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        googleAuthConfig = GoogleAuthConfigStub.createStub();
        googleAuthService = new GoogleAuthService(jwtComponent, googleAuthConfig, restClientBuilder, userRepository, userService);
        // TODO: 그러면 이제 googleAUthconfig도 굳이 stub으로 안만들어도 됨.
    }

    @Test
    public void 구글_리디렉션을_처리할_수_있다() throws Exception {
        // given
        String authCode = "auth-code";
        GoogleTokenResponseDto googleTokenResponse = new GoogleTokenResponseDto("access-token",
            "refresh-token", "id-token", "token-type", 3600);

        UserGoogleAuthResponseDto userGoogleAuthResponse = new UserGoogleAuthResponseDto("googleId",
            "email", true, "name", "givenName", "familyName", "picture", LocalDateTime.now(),
            LocalDateTime.now());

        User user = new User();
        user.setGoogleId("googleId");

        MultiValueMap<String, String> expectedBody = buildMultiValueBody(authCode);

        this.server.expect(requestTo(googleAuthConfig.getOauthUrl()))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8")))
            .andExpect(content().formData(expectedBody))
            .andRespond(withSuccess("{ \"access_token\": \"access-token\" }", MediaType.APPLICATION_JSON));

        // when
        GoogleTokenResponseDto googleTokenResponseDto = googleAuthService.retrieveGoogleToken(authCode);

        // then
        assertThat(googleTokenResponseDto).isNotNull();
        assertThat(googleTokenResponseDto.accessToken()).isEqualTo("access-token");
    }

    private MultiValueMap<String, String> buildMultiValueBody(String authCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", authCode);
        formData.add("client_id", googleAuthConfig.getClientId());
        formData.add("client_secret", googleAuthConfig.getClientSecret());
        formData.add("redirect_uri", googleAuthConfig.getRedirectUri());
        formData.add("grant_type", googleAuthConfig.getGrantType());
        return formData;
    }

    @Test
    public void 구글_토큰을_받아오는_중_에러가_발생하면_예외가_던져진다() {
        // given
        String authCode = "auth-code";

        this.server.expect(requestTo("https://oauth2.googleapis.com/token"))
            .andRespond(withServerError());

        // when & then
        assertThrows(BusinessLogicException.class,
            () -> googleAuthService.retrieveGoogleToken(authCode));
    }

    @Test
    public void 유저정보를_조회할_수_있다() {
        // given
        String accessToken = "access-token";

        this.server.expect(requestTo("https://www.googleapis.com/oauth2/v3/userinfo"))
            .andRespond(withSuccess(
                "{ \"id\": \"googleId\", \"email\": \"email@example.com\", \"name\": \"User Name\" }",
                MediaType.APPLICATION_JSON));

        // when
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = googleAuthService.retrieveUserInfo(
            accessToken);

        // then
        assertThat(userGoogleAuthResponseDto).isNotNull();
        assertThat(userGoogleAuthResponseDto.googleId()).isEqualTo("googleId");
    }


    @Test
    public void 유저정보_조회_중_에러가_발생하면_예외가_던져진다() {
        // given
        String accessToken = "invalid-access-token";

        this.server.expect(requestTo("https://www.googleapis.com/oauth2/v3/userinfo"))
            .andRespond(withServerError());

        // when & then
        assertThrows(BusinessLogicException.class,
            () -> googleAuthService.retrieveUserInfo(accessToken));
    }
}