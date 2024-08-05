package app.cardcapture.payment.common.controller;

import app.cardcapture.ai.bgcolor.dto.WebhookPayload;
import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusRequestDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.security.PrincipleDetails;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "payment common", description = "The single payment API")
@RequestMapping("/api/v1/payment/single")
@RequiredArgsConstructor
public class PaymentCommonController {

    private final PaymentCommonService paymentCommonService;

    @PostMapping("/startCheck")
    @Operation(summary = "결제 가능 확인 및 고유 paymentId 발급",
            description = "사용자의 장바구니의 내용물이 결제 가능한지 확인하고, 결제 가능하다면 고유한 paymentId를 발급합니다. " +
                    "결제 불가능한지는 409(재고 부족), 402(결제 가능 금액 초과), 429(중복 요청), 400(요청이 정합성이 맞지 않음) 상태 코드로 응답합니다." +
                    "요청 시 Product의 price에는 상품 하나당 가격이 들어가야 합니다.")
    public ResponseEntity<SuccessResponseDto<PaymentStartCheckResponseDto>> checkPayment(
            @RequestBody @Valid PaymentStartCheckRequestDto paymentStartCheckRequestDto,
            @AuthenticationPrincipal PrincipleDetails principle
    ) {
        PaymentStartCheckResponseDto paymentStartCheckResponseDto = paymentCommonService.startCheck(paymentStartCheckRequestDto, principle.getUser());
        SuccessResponseDto<PaymentStartCheckResponseDto> response = SuccessResponseDto.create(paymentStartCheckResponseDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/endCheck")
    public ResponseEntity<SuccessResponseDto<PaymentStatusResponseDto>> endCheck(
            @RequestBody @Valid PaymentStatusRequestDto request,
            @AuthenticationPrincipal PrincipleDetails principle
    ) { //TODO: principle로 자기의 구매가 맞는지도 확인 필요한가?
        PaymentStatusResponseDto statusResponse = paymentCommonService.checkPaymentStatus(request.paymentId(), principle.getUser());
        SuccessResponseDto<PaymentStatusResponseDto> response = SuccessResponseDto.create(statusResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    @Hidden
    public ResponseEntity<Void> handleWebhook(
            @RequestBody WebhookPayload payload) {
        paymentCommonService.validateWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
