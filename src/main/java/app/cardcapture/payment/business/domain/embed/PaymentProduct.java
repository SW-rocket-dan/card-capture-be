package app.cardcapture.payment.business.domain.embed;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.DisplayProduct;
import app.cardcapture.payment.business.domain.ProductCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PaymentProduct {

    public PaymentProduct() {
    }

    public PaymentProduct(ProductCategory productCategory, int quantity, int price) {
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.price = price;
        validateProductId();
        validateProductIdAndPrice();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory productCategory;

    @Min(1)
    private int quantity;

    @Min(1)
    private int price;

    public int getTotalPrice() {
        return quantity * price;
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
