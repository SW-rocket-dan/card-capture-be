package app.cardcapture.user.controller;

import app.cardcapture.auth.jwt.dto.JwtAuthorizationDto;
import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.security.PrincipleDetails;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. JWT를 통해 사용자를 식별합니다. ")
    public ResponseEntity<SuccessResponseDto<UserDto>> getUserDetails(
            @AuthenticationPrincipal PrincipleDetails principle
    ) {
        UserDto userDto = UserDto.from(principle.getUser());
        SuccessResponseDto<UserDto> response = SuccessResponseDto.create(userDto);

        return ResponseEntity.ok(response);
    }
}