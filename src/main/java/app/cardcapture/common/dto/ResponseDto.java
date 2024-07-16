package app.cardcapture.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {

    private final ResponseStatus status;
    private final String message;
    private final T data;

    public static <T>ResponseDto<T> createSuccess(String message, T data) {
        return new ResponseDto<>(ResponseStatus.SUCCESS, message, data);
    }

    public static <T>ResponseDto<T> createFailure(String message, T data) {
        return new ResponseDto<>(ResponseStatus.FAILURE, message, data);
    }
}