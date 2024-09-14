package app.cardcapture.payment.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum DisplayProduct {
    AI_POSTER_PRODUCTION_TICKET("AI 포스터 생성 이용권", ProductCategory.AI_POSTER_PRODUCTION_TICKET, 5000, 500),
    POSTER_BUY_TICKET("포스터 구매 이용권", ProductCategory.POSTER_BUY_TICKET, 3000, 200),
    SUBSCRIPTION_TICKET("구독 이용권", ProductCategory.AI_COLOR_CHANGE_TICKET, 10000, 1000); // TODO: 구독제 결정되면 바꿔야함

    private final String koreanName;
    private final ProductCategory productCategory;
    private final int originPrice;
    private final int discountPrice;

    public static Optional<DisplayProduct> fromProductCategoryAndDiscountPrice(ProductCategory uncheckedProductCategory, int discountPrice) {
        return Arrays.stream(values())
                .filter(product -> product.productCategory.equals(uncheckedProductCategory) && product.discountPrice == discountPrice)
                .findFirst();
    }

    public static Optional<DisplayProduct> fromProductCategory(ProductCategory uncheckedProductCategory) {
        return Arrays.stream(values())
                .filter(product -> product.productCategory.equals(uncheckedProductCategory))
                .findFirst();
    }
}
