package app.cardcapture.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

   /*@Operation(summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. " +
                    "JWT를 통해 사용자를 식별합니다. " +
                    "JWT가 유효하지 않으면 401 Unauthorized를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "JWT가 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"JWT가 유효하지 않음\", \"code\": 401}")))})
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(new UserDto("Unauthorized", "JWT가 유효하지 않음", "ab.com"));
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String name = claims.get("name", String.class);
            String email = claims.get("email", String.class);

            UserDto userDto = new UserDto(userId, name, email);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new UserDto("Unauthorized", "JWT가 유효하지 않음", "ab.com"));
        }
    }*/

}
