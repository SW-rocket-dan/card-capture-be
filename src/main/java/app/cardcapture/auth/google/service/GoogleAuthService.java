package app.cardcapture.auth.google.service;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private static final String GOOGLE_TOKEN_RETRIEVAL_ERROR = "Failed to retrieve Google token";
    private static final String USER_INFO_RETRIEVAL_ERROR = "Failed to retrieve user info";
    private static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtComponent jwtComponent;
    private final GoogleAuthConfig googleAuthConfig;
    protected final RestTemplate restTemplate;


    @Transactional //TODO: 왜달아야하는지
    public JwtResponseDto handleGoogleRedirect(String authCode) {
        GoogleTokenResponseDto googleTokenResponseDto = getGoogleToken(authCode);
        UserGoogleAuthResponseDto userGoogleAuthResponseDto = getUserInfo(googleTokenResponseDto.getAccessToken());

        Optional<User> existingUserOpt = userService.findByGoogleId(userGoogleAuthResponseDto.googleId());

        User user = existingUserOpt.orElseGet(() -> userService.save(existingUserOpt.get()));

        String jwt = jwtComponent.createAccessToken(user.getId(), "ROLE_USER",
            Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = jwtComponent.createRefreshToken(user.getId());

        return new JwtResponseDto(jwt, refreshToken);
    }

    public UserGoogleAuthResponseDto getUserInfo(String accessToken) {
        String userInfoUrl = googleAuthConfig.getApiUrl();
        HttpHeaders headers = getHttpHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return getUserResponseWithErrorHandling(userInfoUrl, entity);
    }

    private UserGoogleAuthResponseDto getUserResponseWithErrorHandling(String userInfoUrl,
        HttpEntity<String> entity) {
        try {
            ResponseEntity<UserGoogleAuthResponseDto> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
                entity, UserGoogleAuthResponseDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("{}: {}", USER_INFO_RETRIEVAL_ERROR, e.getMessage(), e);
            throw new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("{}: {}", UNEXPECTED_ERROR, e.getMessage(), e);
            throw new BusinessLogicException(UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private GoogleTokenResponseDto getGoogleToken(String authCode) {
        String tokenUrl = googleAuthConfig.getOauthUrl();
        HttpHeaders headers = getHttpHeaders();
        MultiValueMap<String, String> bodyParams = getGoogleTokenBodyParams(authCode);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParams, headers);

        return getTokenWithExceptionHandling(restTemplate, tokenUrl, request);
    }

    private static GoogleTokenResponseDto getTokenWithExceptionHandling(RestTemplate restTemplate,
        String tokenUrl,
        HttpEntity<MultiValueMap<String, String>> request) {
        try {
            ResponseEntity<GoogleTokenResponseDto> response = restTemplate.postForEntity(
                tokenUrl,
                request,
                GoogleTokenResponseDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("{}: {}", GOOGLE_TOKEN_RETRIEVAL_ERROR, e.getMessage(), e);
            throw new BusinessLogicException(GOOGLE_TOKEN_RETRIEVAL_ERROR, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("{}: {}", UNEXPECTED_ERROR, e.getMessage(), e);
            throw new BusinessLogicException(UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static HttpHeaders getHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> getGoogleTokenBodyParams(String authCode) {
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();

        bodyParams.add("code", authCode);
        bodyParams.add("client_id", googleAuthConfig.getClientId());
        bodyParams.add("client_secret", googleAuthConfig.getClientSecret());
        bodyParams.add("redirect_uri", googleAuthConfig.getRedirectUri());
        bodyParams.add("grant_type", googleAuthConfig.getGrantType());

        return bodyParams;
    }


    public JwtResponseDto refreshJwt(RefreshTokenRequestDto refreshTokenRequest) {
        return null;
    }
}
