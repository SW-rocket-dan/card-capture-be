package app.cardcapture.payment.business.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum DisplayProduct {
    AI_POSTER_PRODUCTION_TICKET("AI 포스터 생성 이용권", "AIP001", 5000, 500),
    POSTER_BUY_TICKET("포스터 구매 이용권", "PBT001", 3000, 200),
    SUBSCRIPTION_TICKET("구독 이용권", "ST001", 10000, 1000); // TODO: 구독제 결정되면 바꿔야함

    private final String koreanName;
    private final String id;
    private final int originPrice;
    private final int discountPrice;

    public static Optional<DisplayProduct> fromIdAndDiscountPrice(String id, int discountPrice) {
        return Arrays.stream(values())
                .filter(product -> product.id.equals(id) && product.discountPrice == discountPrice)
                .findFirst();
    }

    public static Optional<DisplayProduct> fromId(String id) {
        return Arrays.stream(values())
                .filter(product -> product.id.equals(id))
                .findFirst();
    }
}
