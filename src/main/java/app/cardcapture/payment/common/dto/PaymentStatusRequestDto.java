package app.cardcapture.payment.common.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentStatusRequestDto(
        @NotBlank String paymentId
) {
}
