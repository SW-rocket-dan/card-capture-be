package app.cardcapture.auth.google.service;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleTokenRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.dto.RefreshTokenRequestDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.common.utils.TimeUtils;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
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

    private final UserService userService;
    private final JwtComponent jwtComponent;
    private final GoogleAuthConfig googleAuthConfig;
    protected final RestTemplate restTemplate;
    private final RestClient restClient;

    public JwtResponseDto handleGoogleRedirect(String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = getGoogleToken(authCode);
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = getUserInfo(
            googleTokenResponseDto.getAccessToken());

        String googleId = userGoogleAuthResponseDto.googleId();
        boolean existsByGoogleId = userService.existsByGoogleId(googleId);
        User user = processUser(existsByGoogleId, googleId, userGoogleAuthResponseDto);

        return issueJwt(user);
    }

    private JwtResponseDto issueJwt(User user) {
        String jwt = jwtComponent.createAccessToken(user.getId(), "ROLE_USER",
            //TODO: ROLE 객체로 숨기기, 에러코드
            Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = jwtComponent.createRefreshToken(user.getId());

        return new JwtResponseDto(jwt, refreshToken);
    }

    private User processUser(boolean existsByGoogleId, String googleId,
        UserGoogleAuthResponseDto userGoogleAuthResponseDto) {
        if (!existsByGoogleId) {
            User user = new User();

            user.setGoogleId(googleId);
            user.setEmail(userGoogleAuthResponseDto.email());
            user.setName(userGoogleAuthResponseDto.name());
            user.setGivenName(userGoogleAuthResponseDto.givenName());
            user.setFamilyName(userGoogleAuthResponseDto.familyName());
            user.setPicture(userGoogleAuthResponseDto.picture());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            try {
                return userService.save(user);
            } catch (DuplicateKeyException e) {
                throw new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED);
            }
        }

        User user = userService.findByGoogleId(googleId);
        user.setEmail(userGoogleAuthResponseDto.email());
        user.setName(userGoogleAuthResponseDto.name());
        user.setGivenName(userGoogleAuthResponseDto.givenName());
        user.setFamilyName(userGoogleAuthResponseDto.familyName());
        user.setPicture(userGoogleAuthResponseDto.picture());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    public JwtResponseDto refreshJwt(RefreshTokenRequestDto refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();

        Claims claims = jwtComponent.verifyRefreshToken(refreshToken);
        Long userId = Long.valueOf(claims.getId());

        User user = userService.findUserById(userId);
        Date userCreatedAt = TimeUtils.toDate(user.getCreatedAt());

        String newJwt = jwtComponent.createAccessToken(userId, "ROLE_USER", userCreatedAt);
        String newRefreshToken = jwtComponent.createRefreshToken(userId);

        return new JwtResponseDto(newJwt, newRefreshToken);
    }

    private UserGoogleAuthResponseDto getUserInfo(String accessToken) {
        return restClient.get()
            .uri(googleAuthConfig.getApiUrl())
            .headers(headers -> headers.setBearerAuth(accessToken))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED);
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
                throw new BusinessLogicException(ErrorCode.GOOGLE_ACCESS_TOKEN_RETRIEVAL_ERROR);})
            .toEntity(GoogleTokenResponseDto.class)
            .getBody();
    }
}
