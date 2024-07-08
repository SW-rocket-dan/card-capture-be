package app.cardcapture.common.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private int code;

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
