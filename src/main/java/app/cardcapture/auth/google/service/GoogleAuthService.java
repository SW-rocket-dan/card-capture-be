package app.cardcapture.auth.google.service;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private final JwtComponent jwtComponent;
    private final GoogleAuthConfig googleAuthConfig;
    private final RestClient.Builder restClientBuilder;
    private final UserRepository userRepository;
    private final UserService userService;

    public JwtResponseDto handleGoogleRedirect(String authCode) {
        User user = handleGoogleAuthentication(authCode);
        return jwtComponent.issueJwt(user);
    }

    protected UserGoogleAuthResponseDto retrieveUserInfo(String accessToken) {
        return restClientBuilder.build()
            .get()
            .uri(googleAuthConfig.getApiUrl())
            .headers(headers -> headers.setBearerAuth(accessToken))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED);
            })
            .toEntity(UserGoogleAuthResponseDto.class)
            .getBody();
    }

    protected GoogleTokenResponseDto retrieveGoogleToken(String authCode) {
        String tokenUrl = googleAuthConfig.getOauthUrl();

        return restClientBuilder.build()
            .post()
            .uri(tokenUrl)
            .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(buildMultiValueBody(authCode))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(ErrorCode.GOOGLE_ACCESS_TOKEN_RETRIEVAL_ERROR);
            })
            .toEntity(GoogleTokenResponseDto.class)
            .getBody();
    }

    private User handleGoogleAuthentication(String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = retrieveGoogleToken(authCode);
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = retrieveUserInfo(
            googleTokenResponseDto.accessToken());
        String googleId = userGoogleAuthResponseDto.googleId();

        User user = userService.findOrCreateUser(googleId, userGoogleAuthResponseDto);
        userRepository.save(user);

        return user;
    }

    private MultiValueMap<String, String> buildMultiValueBody(
        String authCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("code", authCode);
        formData.add("client_id", googleAuthConfig.getClientId());
        formData.add("client_secret", googleAuthConfig.getClientSecret());
        formData.add("redirect_uri", googleAuthConfig.getRedirectUri());
        formData.add("grant_type", googleAuthConfig.getGrantType());

        return formData;
    }
}
