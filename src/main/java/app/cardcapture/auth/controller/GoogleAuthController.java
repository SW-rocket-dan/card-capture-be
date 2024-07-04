package app.cardcapture.auth.controller;

import app.cardcapture.auth.config.GoogleAuthConfig;
import app.cardcapture.auth.dto.GoogleLoginInfoDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "google login", description = "The google login API")
@RequestMapping("/api/v1/auth/google")
public class GoogleAuthController {

    private final GoogleAuthConfig googleAuthConfig;

    public GoogleAuthController(GoogleAuthConfig googleAuthConfig) {
        this.googleAuthConfig = googleAuthConfig;
    }

    @GetMapping("/login")
    public ResponseEntity<GoogleLoginInfoDto> getGoogleLoginData() {
        GoogleLoginInfoDto googleLoginDto = new GoogleLoginInfoDto(
                googleAuthConfig.getClientId(),
                googleAuthConfig.getRedirectUri(),
                googleAuthConfig.getResponseType(),
                googleAuthConfig.getScope()
        );

        return ResponseEntity.ok(googleLoginDto);
    }


}
