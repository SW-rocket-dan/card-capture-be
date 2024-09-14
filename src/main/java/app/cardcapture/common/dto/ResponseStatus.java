package app.cardcapture.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response paymentStatus")
public enum ResponseStatus {
    SUCCESS, FAILURE
}
