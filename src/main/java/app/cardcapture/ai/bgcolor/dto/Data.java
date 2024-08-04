package app.cardcapture.ai.bgcolor.dto;

public record Data(
        String paymentId,
        String transactionId,
        int totalAmount
) {
}
