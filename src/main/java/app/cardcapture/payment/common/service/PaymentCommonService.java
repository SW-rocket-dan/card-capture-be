package app.cardcapture.payment.common.service;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.entity.Product;
import app.cardcapture.payment.business.dto.ProductDto;
import app.cardcapture.payment.common.domain.entity.Payment;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.common.dto.PaymentTokenRequestDto;
import app.cardcapture.payment.common.dto.PaymentTokenResponseDto;
import app.cardcapture.payment.common.repository.PaymentRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentCommonService {

    private static final String UNVALID_PRODUCT_TOTAL_PRICE = "상품 가격의 합이 일치하지 않습니다.";
    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    @Value("${portone.api.secret}")
    private String apiSecret;

    public PaymentTokenResponseDto createPaymentToken() {
        PaymentTokenRequestDto paymentTokenRequestDto = new PaymentTokenRequestDto(apiSecret);

        PaymentTokenResponseDto paymentTokenResponseDto = restClient.post()
                .uri("https://api.portone.io/login/api-secret")
                .accept(MediaType.APPLICATION_JSON)
                .body(paymentTokenRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new BusinessLogicException(response.getStatusText(), HttpStatus.BAD_REQUEST);
                })
                .body(PaymentTokenResponseDto.class);

        return paymentTokenResponseDto;
    }

    public PaymentStatusResponseDto checkPaymentStatus(String paymentId) {
        return new PaymentStatusResponseDto("PAID");
    }

    public PaymentStartCheckResponseDto startCheck(PaymentStartCheckRequestDto paymentStartCheckRequestDto, User user) {
        String paymentId = UUID.randomUUID().toString();
        while (paymentRepository.existsById(paymentId)) {
            paymentId = UUID.randomUUID().toString();
        }

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setUser(user);

        List<Product> products = paymentStartCheckRequestDto.products()
                .stream()
                .map(ProductDto::toEntity)
                .toList();

        payment.setProducts(products);

        int totalPrice = products.stream()
                .mapToInt(Product::getTotalPrice)
                .sum();
        if (totalPrice != paymentStartCheckRequestDto.totalPrice()) {
            throw new BusinessLogicException(UNVALID_PRODUCT_TOTAL_PRICE, HttpStatus.BAD_REQUEST);
        }

        payment.setTotalPrice(paymentStartCheckRequestDto.totalPrice());
        payment.setRequestTime(paymentStartCheckRequestDto.requestTime());

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentStartCheckResponseDto.from(savedPayment);
    }
}
