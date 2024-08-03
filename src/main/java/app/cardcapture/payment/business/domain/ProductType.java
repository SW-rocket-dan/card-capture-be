package app.cardcapture.payment.business.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum ProductType {
    AI_POSTER_PRODUCTION_TICKET("AI 포스터 생성 이용권", "AIP001", 5000, 500);

    private final String koreanName;
    private final String id;
    private final int originPrice;
    private final int discountPrice;

    public static Optional<ProductType> fromId(String id) {
        return Arrays.stream(values())
                .filter(product -> product.id.equals(id))
                .findFirst();
    }
}
