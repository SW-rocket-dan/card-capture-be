package app.cardcapture.payment.business.dto;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.embed.PaymentProduct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductDto(
    @NotNull ProductCategory productCategory,
    @Min(1) int quantity,
    @Min(1) int price
) {

    public PaymentProduct toEntity() {
        return new PaymentProduct(productCategory(), quantity(), price());
    }
}
