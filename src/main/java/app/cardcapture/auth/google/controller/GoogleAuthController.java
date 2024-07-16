package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.ResponseDto;
import app.cardcapture.user.domain.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "google login", description = "The google login API")
@RequestMapping("/api/v1/auth/google")
@AllArgsConstructor
public class GoogleAuthController {

    private static final String GOOGLE_LOGIN_DATA_RETRIEVAL_SUCCESS = "Google login data retrieved successfully";
    private final GoogleAuthConfig googleAuthConfig;
    private final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtComponent jwtComponent;

    @GetMapping("/login")
    @Operation(summary = "구글 로그인 정보 제공", description = "구글 로그인 정보를 JSON 형태로 반환합니다.")
    public ResponseEntity<ResponseDto<GoogleLoginRequestDto>> getGoogleLoginData() {
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .loginBaseUrl(googleAuthConfig.getBaseUrl())
                .scope(googleAuthConfig.getScope())
                .redirectUri(googleAuthConfig.getRedirectUri())
                .responseType(googleAuthConfig.getResponseType())
                .clientId(googleAuthConfig.getClientId())
                .build();

        ResponseDto responseDto = ResponseDto.createSuccess(GOOGLE_LOGIN_DATA_RETRIEVAL_SUCCESS, googleLoginRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/redirect")
    @Hidden
    public ResponseEntity<JwtDto> getGoogleRedirect(@RequestParam(name = "code") String authCode)
    {
        GoogleTokenResponseDto googleTokenResponseDto = googleAuthService.getGoogleToken(authCode);
        UserDto userDto = googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken());
        User user = userService.save(userDto); // TODO: 만약 이미 가입된 user라면(고유한 googleId이용?) update로 변경?

        String jwt = jwtComponent.create(user.getId(), "ROLE_USER");
        JwtDto jwtDto = new JwtDto(jwt);

        return ResponseEntity.ok(jwtDto);
    }
}
