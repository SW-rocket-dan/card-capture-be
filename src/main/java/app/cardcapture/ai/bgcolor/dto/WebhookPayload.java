package app.cardcapture.ai.bgcolor.dto;

import java.time.ZonedDateTime;

public record WebhookPayload(
        String type,
        ZonedDateTime timestamp,
        Data data) {
}