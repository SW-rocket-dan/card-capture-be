package app.cardcapture.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto<T> {

    private final ResponseStatus status;
    private final ErrorCode errorCode;
    private final String message;

    public static <T>ErrorResponseDto<T> create(ErrorCode errorCode) { // TODO: FAILURE 안씀 제거해도 됨
        return new ErrorResponseDto<>(ResponseStatus.FAILURE, errorCode, errorCode.getMessage());  // TODO: errorcode.getMessage는 사용자에게 보여줄 수 있음. 그래서 Erorcode enum에 메세지 빼고, 마지막에 보내줄 때 사용자 location에 따라 다른 메세지를 보내줄 수 있게 하자.
    }
}
