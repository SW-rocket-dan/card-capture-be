package app.cardcapture.payment.business.dto;

import app.cardcapture.payment.business.domain.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductDto(
        @NotBlank String productId,
        @Min(1) int quantity,
        @Min(1) int price
) {
    public Product toEntity() {
        return new Product(productId(), quantity(), price());
    }
}
