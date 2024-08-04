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
    private final static String UNVALID_PRODUCT_PRICE = "유효한 상품 가격이 아닙니다.";

    public Product toEntity() { // TODO: Entity안으로 옮기고, Entity가 이 검증작업 하도록
        validateProductId();
        validateProductIdAndPrice();
        return new Product(productId(), quantity(), price());
    }

    private void validateProductId() {
        ProductType.fromId(productId)
                .orElseThrow(() -> new BusinessLogicException(UNVALID_PRODUCT_ID, HttpStatus.BAD_REQUEST));
    }

    private void validateProductIdAndPrice() {
        ProductType.fromIdAndDiscountPrice(productId, price)
                .orElseThrow(() -> new BusinessLogicException(UNVALID_PRODUCT_PRICE, HttpStatus.BAD_REQUEST));
    }
}
