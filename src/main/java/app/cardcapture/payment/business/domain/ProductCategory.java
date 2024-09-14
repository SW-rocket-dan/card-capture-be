package app.cardcapture.payment.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductCategory {
    AI_POSTER_PRODUCTION_TICKET("AI 포스터 생성 이용권"),
    POSTER_BUY_TICKET("포스터 구매 이용권"),
    CREDIT("크레딧"),
    AI_COLOR_CHANGE_TICKET("AI 색상 변경 이용권"),
    FAST_QUEUE_TICKET("빠른 대기열 이용권");

    private final String koreanName;
}
