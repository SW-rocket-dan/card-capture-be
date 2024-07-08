package app.cardcapture.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message, HttpStatus status) {
        super(message);
    }
}
