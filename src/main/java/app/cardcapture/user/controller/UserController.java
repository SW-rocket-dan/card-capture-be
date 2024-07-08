package app.cardcapture.user.controller;

import app.cardcapture.auth.jwt.dto.JwtAuthorizationDto;
import app.cardcapture.common.dto.ErrorResponse;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    private final UserService userService;

    @Operation(summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. " +
                    "JWT를 통해 사용자를 식별합니다. " +
                    "JWT가 유효하지 않으면 403을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "403", description = "JWT가 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"JWT가 유효하지 않음\", \"code\": 403}")))})
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserDetails(@RequestHeader(value="Authorization") @Valid JwtAuthorizationDto jwtAuthorizationDto) {
        System.out.println("jwtAuthorizationDto = " + jwtAuthorizationDto);
        String accessToken = jwtAuthorizationDto.getAceessToken();
        UserDto userDto = userService.findUserByAccessToken(accessToken);

        return ResponseEntity.ok(userDto);
    }
}
