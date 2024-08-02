package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.config.GoogleAuthConfig;
import app.cardcapture.auth.google.dto.GoogleLoginRequestDto;
import app.cardcapture.auth.google.dto.GoogleTokenResponseDto;
import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtDto;
import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    } // TODO: 쿠키에 넣어주면 프론트가 편할 수 있다 고려해볼 것

    @GetMapping("/redirect") // TODO: 만약 access token이 만료됐으면, 프론트에서 refreshtoken이 있으면 보내달라는 의미로 에러코드를 다른 걸로 보내줘야함(상황에 따라 프론트에서 기대되는 동작이 다르기 때문에 이를 사전에 정해놓는 것 필요)
    // 지금은 다 403보내고있는데, 이를 구분해주기위한 용도로 에러코드를 이용할 수 있다.
    @Operation(summary = "구글 리다이렉트 엔드포인트", description = "구글 리다이렉트를 통해 받은 auth code를 받습니다. auth code를 이용하여 유저 정보를 가져올 것입니다.")
    @Transactional //TODO: 추후 serivce로 분리하면 service계층에만 transactional달기
    public ResponseEntity<SuccessResponseDto<JwtDto>> getGoogleRedirect(@RequestParam(name = "code") String authCode)
        {
            // TODO: Service로 좀 감추기 (테스트 관점에서도 복잡도 감소 가능)
            GoogleTokenResponseDto googleTokenResponseDto = googleAuthService.getGoogleToken(authCode);
            UserDto userDto = googleAuthService.getUserInfo(googleTokenResponseDto.getAccessToken());

            Optional<User> existingUserOpt = userService.findByGoogleId(userDto.getGoogleId());

            User user = existingUserOpt.orElseGet(() -> {
                // 신규 유저라면 저장
                return userService.save(userDto);
            });

            String jwt = jwtComponent.create(user.getId(), "ROLE_USER", Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
            JwtDto jwtDto = new JwtDto(jwt);

            SuccessResponseDto responseDto = SuccessResponseDto.create(jwtDto);

            return ResponseEntity.ok(responseDto);
    }
}
