package app.cardcapture.payment.business.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NewUserProductCategory {
    FREE_AI_POSTER_PRODUCTION_TICKET(ProductCategory.AI_POSTER_PRODUCTION_TICKET, 5);

    private final ProductCategory productCategory;
    private final int count;
}
