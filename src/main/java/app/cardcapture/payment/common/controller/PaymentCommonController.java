package app.cardcapture.payment.common.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusRequestDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.common.service.PaymentCommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
                    "결제 불가능한지는 40x(재고 부족), 402(결제 가능 금액 초과), 429(중복 요청), 400(요청이 정합성이 맞지 않음) 상태 코드로 응답합니다.")
    public ResponseEntity<SuccessResponseDto<PaymentStartCheckResponseDto>> checkPayment(
            @RequestBody @Valid PaymentStartCheckRequestDto paymentStartCheckRequestDto
    ) {
        /*if (!hasSufficientInventory(paymentStartCheckRequestDto.getAmount())) {
            return ResponseEntity.status(400).body("재고가 부족합니다.");
        }
        if (exceedsPaymentLimit(paymentStartCheckRequestDto.getAmount())) {
            return ResponseEntity.status(402).body("결제 가능 금액을 초과했습니다.");
        }
        if (isDuplicateRequest(paymentStartCheckRequestDto)) {
            return ResponseEntity.status(429).body("너무 빠르게 중복 요청되었습니다.");
        }
        try {
            // Payment processing logic...
            return ResponseEntity.ok("결제 가능");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("결제 요청 중 오류가 발생했습니다.");
        }*/

        PaymentStartCheckResponseDto paymentStartCheckResponseDto = paymentCommonService.startCheck(paymentStartCheckRequestDto);
        SuccessResponseDto<PaymentStartCheckResponseDto> response = SuccessResponseDto.create(paymentStartCheckResponseDto);
        return ResponseEntity.ok(response);
    }


    /*
    private boolean hasSufficientInventory(int totalPrice) {
        // Implement inventory check logic
        return true;
    }

    private boolean exceedsPaymentLimit(int totalPrice) {
        // Implement payment limit check logic
        return false;
    }

    private boolean isDuplicateRequest(PaymentRequest request) {
        // Implement duplicate request check logic
        return false;
    }*/

    @PostMapping("/endCheck")
    public ResponseEntity<SuccessResponseDto<PaymentStatusResponseDto>> endCheck(
            @RequestBody @Valid PaymentStatusRequestDto request) { //TODO: principle로 자기의 구매가 맞는지도 확인 필요한가?
        try {
            PaymentStatusResponseDto statusResponse = paymentCommonService.checkPaymentStatus(request.paymentId());
            return ResponseEntity.ok(SuccessResponseDto.create(statusResponse));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(SuccessResponseDto.create(new PaymentStatusResponseDto("ERROR")));
        }
    }
}
