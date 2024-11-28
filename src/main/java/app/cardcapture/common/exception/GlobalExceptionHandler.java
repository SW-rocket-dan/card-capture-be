package app.cardcapture.common.exception;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        log.error("BusinessLogicException: {}",ex.getMessage(), ex.getCause(), ex);

        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponseDto<String> response = ErrorResponseDto.create(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage(), ex.getCause(), ex);

        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        ErrorResponseDto<String> response = ErrorResponseDto.create(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex.getCause(), ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.REQUEST_VALIDATION_FAILED;
        ErrorResponseDto<String> response = ErrorResponseDto.create(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleGeneralException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex.getCause(), ex);

        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        ErrorResponseDto<String> response = ErrorResponseDto.create(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInterruptedException(InterruptedException ex) {
        log.error("InterruptedException: {}", ex.getMessage(), ex.getCause(), ex);

        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        ErrorResponseDto<String> response = ErrorResponseDto.create(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
