package app.cardcapture.auth.google.controller;

import app.cardcapture.auth.google.service.GoogleAuthService;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.dto.RefreshTokenRequestDto;
import app.cardcapture.common.dto.SuccessResponseDto;
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

@RestController
@Tag(name = "google login", description = "The google login API")
@RequestMapping("/api/v1/auth/google")
@AllArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping("/redirect")
    @Operation(summary = "구글 리다이렉트 엔드포인트", description = "구글 리다이렉트를 통해 받은 auth code를 받습니다. auth code를 이용하여 유저 정보를 가져올 것입니다.")
    @Transactional
    public ResponseEntity<SuccessResponseDto<JwtResponseDto>> getGoogleRedirect(
            @RequestParam(name = "code") String authCode
    ) {
        JwtResponseDto jwtResponseDto = googleAuthService.handleGoogleRedirect(authCode);
        return ResponseEntity.ok(SuccessResponseDto.create(jwtResponseDto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "JWT 갱신", description = "리프레시 토큰을 이용하여 새로운 JWT를 반환합니다.")
    public ResponseEntity<SuccessResponseDto<JwtResponseDto>> refreshJwt(
            @RequestBody @Valid RefreshTokenRequestDto refreshTokenRequest
    ) {
        JwtResponseDto jwtResponseDto = googleAuthService.refreshJwt(refreshTokenRequest);
        return ResponseEntity.ok(SuccessResponseDto.create(jwtResponseDto));
    }
}