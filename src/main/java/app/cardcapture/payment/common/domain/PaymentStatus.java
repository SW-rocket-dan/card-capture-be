package app.cardcapture.payment.common.domain;

public enum PaymentStatus {
    ARRIVED, READY, CANCELLED, FAILED, PAY_PENDING, PAID, PARTIAL_CANCELLED, SERVER_REQUEST_CANCELLED, FINAL_PAID;
}
