package app.cardcapture.common.dto;

public class SuccessResponse {
    private String message;
    private int code;

    public SuccessResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
