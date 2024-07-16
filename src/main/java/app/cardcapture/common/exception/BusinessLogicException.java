package app.cardcapture.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessLogicException extends RuntimeException {
    private final HttpStatus status;

    public BusinessLogicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}