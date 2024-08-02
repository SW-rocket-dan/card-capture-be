package app.cardcapture.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto<T> {

    private final ResponseStatus status;
    private final String message;
    private final T data;

    public static <T>ErrorResponseDto<T> create(String message, T data) {
        return new ErrorResponseDto<>(ResponseStatus.FAILURE, message, data);
    }
}
