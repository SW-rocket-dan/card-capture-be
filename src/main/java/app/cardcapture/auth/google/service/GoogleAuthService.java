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
import app.cardcapture.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public JwtResponseDto handleGoogleRedirect(String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = getGoogleToken(authCode);
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = getUserInfo(
            googleTokenResponseDto.getAccessToken());

        String googleId = userGoogleAuthResponseDto.googleId();
        User user = findOrCreateUser(googleId, userGoogleAuthResponseDto);

        userRepository.save(user);

        return issueJwt(user);
    }

    private User findOrCreateUser(String googleId,
        UserGoogleAuthResponseDto userGoogleAuthResponseDto) {
        return userRepository.findByGoogleId(googleId)
            .map(existingUser -> updateUser(existingUser, userGoogleAuthResponseDto))
            .orElseGet(() -> createUser(googleId, userGoogleAuthResponseDto));
    }

    private User updateUser(User user, UserGoogleAuthResponseDto dto) {
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setGivenName(dto.givenName());
        user.setFamilyName(dto.familyName());
        user.setPicture(dto.picture());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    private User createUser(String googleId, UserGoogleAuthResponseDto dto) {
        User newUser = new User();

        newUser.setGoogleId(googleId);
        newUser.setEmail(dto.email());
        newUser.setName(dto.name());
        newUser.setGivenName(dto.givenName());
        newUser.setFamilyName(dto.familyName());
        newUser.setPicture(dto.picture());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    private JwtResponseDto issueJwt(User user) {
        String jwt = jwtComponent.createAccessToken(user.getId(), user.getRoles(),
            Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = jwtComponent.createRefreshToken(user.getId());

        return new JwtResponseDto(jwt, refreshToken);
    }

    public JwtResponseDto refreshJwt(RefreshTokenRequestDto refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();

        Claims claims = jwtComponent.verifyRefreshToken(refreshToken);
        Long userId = Long.valueOf(claims.getId());

        User user = userService.findUserById(userId);
        Date userCreatedAt = TimeUtils.toDate(user.getCreatedAt());

        String newJwt = jwtComponent.createAccessToken(userId, user.getRoles(), userCreatedAt);
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

    private MultiValueMap<String, String> convertToFormData(
        GoogleTokenRequestDto googleTokenRequestDto) {
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
            .body(convertToFormData(googleTokenRequestDto))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new BusinessLogicException(ErrorCode.GOOGLE_ACCESS_TOKEN_RETRIEVAL_ERROR);
            })
            .toEntity(GoogleTokenResponseDto.class)
            .getBody();
    }
}
