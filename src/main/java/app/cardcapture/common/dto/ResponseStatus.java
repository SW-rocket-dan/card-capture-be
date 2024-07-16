package app.cardcapture.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response status")
public enum ResponseStatus {
    SUCCESS, FAILURE
}
