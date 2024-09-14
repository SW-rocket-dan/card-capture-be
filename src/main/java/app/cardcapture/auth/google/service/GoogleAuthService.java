package app.cardcapture.auth.google.service;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleTokenRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.dto.RefreshTokenRequestDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserMapper;
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.service.UserService;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private static final String GOOGLE_TOKEN_RETRIEVAL_ERROR = "Failed to retrieve Google token";
    private static final String USER_INFO_RETRIEVAL_ERROR = "Failed to retrieve user info";

    private final UserService userService;
    private final JwtComponent jwtComponent;
    private final GoogleAuthConfig googleAuthConfig;
    protected final RestTemplate restTemplate;
    private final RestClient restClient;


    @Transactional
    public JwtResponseDto handleGoogleRedirect(String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = getGoogleToken(authCode);
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = getUserInfo(
            googleTokenResponseDto.getAccessToken());

        Optional<User> existingUserOpt = userService.findByGoogleId(
            userGoogleAuthResponseDto.googleId());

        User user = existingUserOpt.orElseGet(() -> userService.save(existingUserOpt.get()));

        String jwt = jwtComponent.createAccessToken(user.getId(), "ROLE_USER",
            Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = jwtComponent.createRefreshToken(user.getId());

        return new JwtResponseDto(jwt, refreshToken);
    }

    public UserGoogleAuthResponseDto getUserInfo(String accessToken) {
        return restClient.get()
            .uri(googleAuthConfig.getApiUrl())
            .headers(headers -> headers.setBearerAuth(accessToken))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.BAD_REQUEST);
            })
            .toEntity(UserGoogleAuthResponseDto.class)
            .getBody();
    }

    private GoogleTokenResponseDto getGoogleToken(String authCode) {
        String tokenUrl = googleAuthConfig.getOauthUrl();
        GoogleTokenRequestDto googleTokenRequestDto = new GoogleTokenRequestDto(
            authCode,
            googleAuthConfig.getClientId(),
            googleAuthConfig.getClientSecret(),
            googleAuthConfig.getRedirectUri(),
            googleAuthConfig.getGrantType());

        return restClient.post()
            .uri(tokenUrl)
            .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(googleTokenRequestDto)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(GOOGLE_TOKEN_RETRIEVAL_ERROR, HttpStatus.BAD_REQUEST);
            })
            .toEntity(GoogleTokenResponseDto.class)
            .getBody();
    }

    public JwtResponseDto refreshJwt(RefreshTokenRequestDto refreshTokenRequest) {
        return null;
    }
}
