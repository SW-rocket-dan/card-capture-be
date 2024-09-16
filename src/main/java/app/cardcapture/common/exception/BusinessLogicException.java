package app.cardcapture.common.exception;

import app.cardcapture.common.dto.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessLogicException extends RuntimeException {
    private final ErrorCode errorCode;
}