package app.cardcapture.payment.business.dto;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.ProductType;
import app.cardcapture.payment.business.domain.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;

public record ProductDto(
        @NotBlank String productId,
        @Min(1) int quantity,
        @Min(1) int price
) {
    private final static String UNVALID_PRODUCT_ID = "유효한 상품 ID가 아닙니다.";

    public Product toEntity() {
        ProductType.fromId(productId)
                .orElseThrow(() -> new BusinessLogicException(UNVALID_PRODUCT_ID, HttpStatus.BAD_REQUEST));

        return new Product(productId(), quantity(), price());
    }
}
