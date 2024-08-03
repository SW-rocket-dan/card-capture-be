package app.cardcapture.payment.common.dto;

import app.cardcapture.payment.business.dto.ProductDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentStartCheckRequestDto(
        @NotEmpty @Valid List<ProductDto> products,
        @Min(1) int totalPrice,
        LocalDateTime requestTime
) {

}
