package app.cardcapture.auth.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenBlacklistedException extends RuntimeException {
    public TokenBlacklistedException(String message) {
        super(message);
    }
}
