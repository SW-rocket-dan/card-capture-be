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
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        String jwt = jwtComponent.createAccessToken(user.getId(), Role.USER, // TODO: ROLE.USER바꿔야함
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

            //TODO: UserRoleRespository에 저장해야하고 이 메서드 UserSerivce로 옮기든지하고 Transaction달아야할듯
            return userService.save(user);
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

        String newJwt = jwtComponent.createAccessToken(userId, Role.USER, userCreatedAt);// TODO: ROLE.USER바꿔야함
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

    private MultiValueMap<String, String> convertToFormData(GoogleTokenRequestDto googleTokenRequestDto) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", googleTokenRequestDto.getCode());
        formData.add("client_id", googleTokenRequestDto.getClientId());
        formData.add("client_secret", googleTokenRequestDto.getClientSecret());
        formData.add("redirect_uri", googleTokenRequestDto.getRedirectUri());
        formData.add("grant_type", googleTokenRequestDto.getGrantType());
        return formData;
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
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(convertToFormData(googleTokenRequestDto))  // DTO를 formData로 변환
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(ErrorCode.GOOGLE_ACCESS_TOKEN_RETRIEVAL_ERROR);})
            .toEntity(GoogleTokenResponseDto.class)
            .getBody();
    }
}
