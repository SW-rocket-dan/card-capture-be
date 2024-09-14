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
    } // TODO: https://velog.io/@ollie221/Error-Code-%EC%A0%81%EC%9A%A9%EA%B8%B0 status만으로 판단하기 어려운 경우 에러 코드를 직접 정의해서 클라이언트에서 에러 상황에 따라 행동할 수 있게함
}
