package app.cardcapture.auth.jwt.controller;

import app.cardcapture.auth.jwt.service.TokenBlacklistService;
import app.cardcapture.common.dto.SuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "token", description = "The token API")
@RequestMapping("/api/v1/token")
@AllArgsConstructor
public class TokenController {

    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "JWT 토큰을 블랙리스트에 추가하여 로그아웃 처리합니다.")
    public ResponseEntity<SuccessResponseDto<String>> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        tokenBlacklistService.addToBlacklist(authHeader);
        return ResponseEntity.ok(SuccessResponseDto.create("로그아웃 성공"));
    }
}
