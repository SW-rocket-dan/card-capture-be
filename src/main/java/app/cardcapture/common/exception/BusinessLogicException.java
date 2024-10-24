package app.cardcapture.common.exception;

import app.cardcapture.common.dto.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) { // TODO: e를 같이 BusinessLogicException로 넘겨줘야 함 BusinessLogicException가 상속하는 RunTimeException보면 Throwable cause을 생성자로 받고 있음 이걸 넘겨주고  Handler에서 log.error 찍어줘야 나중에 디버깅 가능
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}