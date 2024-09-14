package app.cardcapture.payment.portone.dto;

import app.cardcapture.common.utils.TimeUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record PortoneWebhookReqeustDto(
    @NotNull String type,
    @NotNull ZonedDateTime timestamp,
    @NotNull @Valid PortonePaymentRequestDto data
) {

    public PortoneWebhookReqeustDto {
        timestamp = TimeUtils.toSeoulZonedDateTime(timestamp);
    }
}