package app.cardcapture.payment.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductCategory {
    AI_POSTER_PRODUCTION_TICKET("AI 포스터 생성 이용권","UAPC001"),
    POSTER_BUY_TICKET("포스터 구매 이용권","UAPC002"),
    CREDIT("크레딧","UAPC003"),
    AI_COLOR_CHANGE_TICKET("AI 색상 변경 이용권","UAPC004"),
    FAST_QUEUE_TICKET("빠른 대기열 이용권","UAPC005"),;

    private final String koreanName;
    private final String code;
}
