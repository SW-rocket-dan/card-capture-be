package app.cardcapture.payment.portone.dto;

import app.cardcapture.common.utils.TimeUtils;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

public record PortonePaymentInquiryResponseDto(
    String status,
    String id,
    String transactionId,
    String merchantId,
    String storeId,
    PaymentMethod method,
    Channel channel,
    String version,
    List<Webhook> webhooks,
    @NotNull ZonedDateTime requestedAt,
    ZonedDateTime updatedAt,
    ZonedDateTime statusChangedAt,
    String orderName,
    Amount amount,
    String currency,
    Customer customer,
    String promotionId,
    boolean isCulturalExpense,
    ZonedDateTime paidAt,
    String pgTxId,
    String pgResponse,
    String receiptUrl
) {

    public PortonePaymentInquiryResponseDto {
        requestedAt = TimeUtils.toSeoulZonedDateTime(requestedAt);
        updatedAt = TimeUtils.toSeoulZonedDateTime(updatedAt);
        statusChangedAt = TimeUtils.toSeoulZonedDateTime(statusChangedAt);

        if (paidAt != null) {
            paidAt = TimeUtils.toSeoulZonedDateTime(paidAt);
        }
    }

    public record PaymentMethod(
        String type,
        Card card,
        String approvalNumber,
        Installment installment,
        boolean pointUsed
    ) {

        public record Card(
            String publisher,
            String issuer,
            String brand,
            String type,
            String ownerType,
            String bin,
            String name,
            String number
        ) {

        }

        public record Installment(
            int month,
            boolean isInterestFree
        ) {

        }
    }

    public record Channel(
        String type,
        String id,
        String key,
        String name,
        String pgProvider,
        String pgMerchantId
    ) {

    }

    public record Webhook(
        String paymentStatus,
        String id,
        String status,
        String url,
        boolean isAsync,
        int currentExecutionCount,
        Request request,
        Response response,
        ZonedDateTime triggeredAt
    ) {

        public Webhook {
            if (triggeredAt != null) {
                triggeredAt = TimeUtils.toSeoulZonedDateTime(triggeredAt);
            }
        }

        public record Request(
            String header,
            String body,
            ZonedDateTime requestedAt
        ) {

            public Request {
                if (requestedAt != null) {
                    requestedAt = TimeUtils.toSeoulZonedDateTime(requestedAt);
                }
            }
        }

        public record Response(
            String code,
            String header,
            String body,
            ZonedDateTime respondedAt
        ) {

            public Response {
                if (respondedAt != null) {
                    respondedAt = TimeUtils.toSeoulZonedDateTime(respondedAt);
                }
            }
        }
    }

    public record Amount(
        int total,
        int taxFree,
        int vat,
        int supply,
        int discount,
        int paid,
        int cancelled,
        int cancelledTaxFree
    ) {

    }

    public record Customer(
        String id
    ) {

    }
}
