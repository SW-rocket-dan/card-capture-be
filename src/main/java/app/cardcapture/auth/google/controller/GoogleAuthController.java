package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.dto.RefreshTokenRequestDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.common.utils.TimeUtils;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@RestController
@Tag(name = "google login", description = "The google login API")
@RequestMapping("/api/v1/auth/google")
@AllArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthConfig googleAuthConfig;
    private final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtComponent jwtComponent;

    @GetMapping("/login")
    @Operation(summary = "구글 로그인 정보 제공", description = "구글 로그인 정보를 JSON 형태로 반환합니다.")
    public ResponseEntity<SuccessResponseDto<GoogleLoginRequestDto>> getGoogleLoginData() {
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .loginBaseUrl(googleAuthConfig.getBaseUrl())
                .scope(googleAuthConfig.getScope())
                .redirectUri(googleAuthConfig.getRedirectUri())
                .responseType(googleAuthConfig.getResponseType())
                .clientId(googleAuthConfig.getClientId())
                .build();
        SuccessResponseDto responseDto = SuccessResponseDto.create(googleLoginRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/redirect")
    @Operation(summary = "구글 리다이렉트 엔드포인트", description = "구글 리다이렉트를 통해 받은 auth code를 받습니다. auth code를 이용하여 유저 정보를 가져올 것입니다.")
    @Transactional
    public ResponseEntity<SuccessResponseDto<JwtResponseDto>> getGoogleRedirect(
            @RequestParam(name = "code") String authCode
    ) {
        GoogleTokenResponseDto googleTokenResponseDto = googleAuthService.getGoogleToken(authCode);
        UserDto userDto = googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken());

        Optional<User> existingUserOpt = userService.findByGoogleId(userDto.getGoogleId());

        User user = existingUserOpt.orElseGet(() -> {
            return userService.save(userDto);
        });

        String jwt = jwtComponent.createAccessToken(user.getId(), "ROLE_USER", Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = jwtComponent.createRefreshToken(user.getId());
        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwt, refreshToken);

        SuccessResponseDto responseDto = SuccessResponseDto.create(jwtResponseDto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "JWT 갱신", description = "리프레시 토큰을 이용하여 새로운 JWT를 반환합니다.")
    public ResponseEntity<SuccessResponseDto<JwtResponseDto>> refreshJwt(
            @RequestBody @Valid RefreshTokenRequestDto refreshTokenRequest
    ) {
        String refreshToken = refreshTokenRequest.refreshToken();

        Claims claims = jwtComponent.verifyRefreshToken(refreshToken);

        Long userId = claims.getId();
        UserDto userDto = userService.findUserById(userId);

        Date userCreatedAt = TimeUtils.toDate(userDto.getCreatedAt());
        String newJwt = jwtComponent.createAccessToken(userId, "ROLE_USER", userCreatedAt);
        String newRefreshToken = jwtComponent.createRefreshToken(userId);

        JwtResponseDto jwtResponseDto = new JwtResponseDto(newJwt, newRefreshToken);
        SuccessResponseDto responseDto = SuccessResponseDto.create(jwtResponseDto);

        return ResponseEntity.ok(responseDto);
    }
}