package app.cardcapture.payment.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PortonePaymentRequestDto(
    @NotBlank @Size(max=300) String paymentId,
    @NotBlank @Size(max=300) String transactionId,
    int totalAmount
) {

}
