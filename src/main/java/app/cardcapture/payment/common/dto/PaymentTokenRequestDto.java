package app.cardcapture.payment.common.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentTokenRequestDto(
    @NotBlank String apiSecret
) {
}
