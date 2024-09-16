package app.cardcapture.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto<T> {

    private final ResponseStatus status;
    private final ErrorCode errorCode;
    private final String message;

    public static <T>ErrorResponseDto<T> create(ErrorCode errorCode) {
        return new ErrorResponseDto<>(ResponseStatus.FAILURE, errorCode, errorCode.getMessage());
    }
}
