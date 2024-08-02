package app.cardcapture.common.exception;

import app.cardcapture.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleBusinessLogicException(BusinessLogicException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponseDto<String> response = ErrorResponseDto.create(ex.getMessage(), null);
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto<String>> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponseDto<String> response = ErrorResponseDto.create(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
