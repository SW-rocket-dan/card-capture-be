package app.cardcapture.payment.business.dto;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.DisplayProduct;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.embed.PaymentProduct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

public record ProductDto(
        @NotNull ProductCategory productCategory,
        @Min(1) int quantity,
        @Min(1) int price
) {
    private final static String UNVALID_PRODUCT_ID = "유효한 상품 ID가 아닙니다.";
    private final static String UNVALID_PRODUCT_PRICE = "유효한 상품 가격이 아닙니다.";

    public PaymentProduct toEntity() { // TODO: Entity안으로 옮기고, Entity가 이 검증작업 하도록
        validateProductId();
        validateProductIdAndPrice();
        return new PaymentProduct(productCategory(), quantity(), price());
    }

    private void validateProductId() {
        DisplayProduct.fromProductCategory(productCategory)
                .orElseThrow(() -> new BusinessLogicException(UNVALID_PRODUCT_ID, HttpStatus.BAD_REQUEST));
    }

    private void validateProductIdAndPrice() {
        DisplayProduct.fromProductCategoryAndDiscountPrice(productCategory, price)
                .orElseThrow(() -> new BusinessLogicException(UNVALID_PRODUCT_PRICE, HttpStatus.BAD_REQUEST));
    }
}
