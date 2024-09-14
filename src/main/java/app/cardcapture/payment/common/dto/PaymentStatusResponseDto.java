package app.cardcapture.payment.common.dto;

import app.cardcapture.payment.common.domain.PaymentStatus;

public record PaymentStatusResponseDto(
    PaymentStatus paymentStatus
) {

}
