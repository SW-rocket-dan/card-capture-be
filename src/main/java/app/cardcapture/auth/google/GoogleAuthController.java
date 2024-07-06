package app.cardcapture.auth.google;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtDto;
import app.cardcapture.auth.jwt.service.JwtService;
import app.cardcapture.user.dto.UserDto;
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

    private final GoogleAuthConfig googleAuthConfig;
    private final GoogleAuthService googleAuthService;
    private final JwtService jwtService;

    @GetMapping("/login")
    @Operation(summary = "구글 로그인 정보 제공",
            description = "구글 로그인 정보를 JSON 형태로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "구글 로그인 정보 반환",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<GoogleLoginRequestDto> getGoogleLoginData() {
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .loginBaseUrl(googleAuthConfig.getBaseUrl())
                .scope(googleAuthConfig.getScope())
                .redirectUri(googleAuthConfig.getRedirectUri())
                .responseType(googleAuthConfig.getResponseType())
                .clientId(googleAuthConfig.getClientId())
                .build();

        return ResponseEntity.ok(googleLoginRequestDto);
    }

    @GetMapping("/redirect")
    @Hidden
    public ResponseEntity<JwtDto> getGoogleRedirect(@RequestParam(name = "code") String authCode)
    {
        System.out.println("authCode = " + authCode);
        // TODO: 구글 데이터 서버에 auth code 보내기 -> access token & refresh token 받기 -> 데이터 서버에 access token으로 사용자 정보 요청하기 (at와 ft는 폐기)
        GoogleTokenResponseDto googleTokenResponseDto = googleAuthService.getGoogleToken(authCode);
        System.out.println("googleTokenResponseDto = " + googleTokenResponseDto.toString());

        // TODO: 자체 Jwt 발급하기
        UserDto userDto = googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken());
        // jwt 발급 시 필요한 데이터 모아서 보내기
        JwtDto jwtDto = jwtService.publish(userDto.getId()); //메서드 명은 고민 더 해보기

        return ResponseEntity.ok(jwtDto);
    }

}
