package app.cardcapture.payment.business.dto;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.DisplayProduct;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.embed.PaymentProduct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductDto(
    @NotNull ProductCategory productCategory,
    @Min(1) int quantity,
    @Min(1) int price
) {

    public PaymentProduct toEntity() { // TODO: Entity안으로 옮기고, Entity가 이 검증작업 하도록
        validateProductId();
        validateProductIdAndPrice();
        return new PaymentProduct(productCategory(), quantity(), price());
    }

    private void validateProductId() {
        DisplayProduct.fromProductCategory(productCategory)
            .orElseThrow(
                () -> new BusinessLogicException(ErrorCode.NON_EXISTENT_PRODUCT_ID));
    }

    private void validateProductIdAndPrice() {
        DisplayProduct.fromProductCategoryAndDiscountPrice(productCategory, price)
            .orElseThrow(
                () -> new BusinessLogicException(ErrorCode.INVALID_PRODUCT_PRICE));
    }
}
