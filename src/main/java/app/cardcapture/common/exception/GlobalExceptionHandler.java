package app.cardcapture.common.exception;

import app.cardcapture.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleBusinessLogicException(BusinessLogicException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponseDto<String> response = ErrorResponseDto.create(ex.getMessage(), null);
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponseDto<String> response = ErrorResponseDto.create(errorMessage, null);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleGeneralException(RuntimeException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponseDto<String> response = ErrorResponseDto.create(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
