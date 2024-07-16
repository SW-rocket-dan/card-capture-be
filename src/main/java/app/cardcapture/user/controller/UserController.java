package app.cardcapture.user.controller;

import app.cardcapture.auth.jwt.dto.JwtAuthorizationDto;
import app.cardcapture.common.dto.ResponseDto;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private static final String USER_INFO_RETRIEVAL_SUCCESS = "User details retrieved successfully";
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. " +
                    "JWT를 통해 사용자를 식별합니다. " +
                    "JWT가 유효하지 않으면 403을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "403", description = "JWT가 유효하지 않음")})
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserDto>> getUserDetails(
            @RequestHeader(value = "Authorization")
            @Valid JwtAuthorizationDto jwtAuthorizationDto
    ) {
        String accessToken = jwtAuthorizationDto.getAceessToken();
        UserDto userDto = userService.findUserByAccessToken(accessToken);
        ResponseDto<UserDto> response = ResponseDto.createSuccess(USER_INFO_RETRIEVAL_SUCCESS, userDto);

        return ResponseEntity.ok(response);
    }
}