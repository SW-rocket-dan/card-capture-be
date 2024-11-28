package app.cardcapture.payment.business.dto;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserProductCategoryDto(
    @NotNull ProductCategory productCategory,
    @Min(1) int quantity
) {

    public static UserProductCategoryDto from(UserProductCategory userProductCategory) {
        ProductCategory productCategory = userProductCategory.getProductCategory();
        return new UserProductCategoryDto(productCategory, userProductCategory.getQuantity());
    }
}
