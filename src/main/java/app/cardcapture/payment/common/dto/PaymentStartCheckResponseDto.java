package app.cardcapture.payment.common.dto;

import app.cardcapture.payment.common.domain.entity.Payment;
import jakarta.validation.constraints.NotBlank;

public record PaymentStartCheckResponseDto(
    @NotBlank String paymentId
) {
    public static PaymentStartCheckResponseDto from(Payment payment) {
        return new PaymentStartCheckResponseDto(payment.getId());
    }
}
