package app.cardcapture.payment.common.service;

import app.cardcapture.ai.bgcolor.dto.WebhookPayload;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.entity.Product;
import app.cardcapture.payment.business.dto.ProductDto;
import app.cardcapture.payment.common.config.PaymentConfig;
import app.cardcapture.payment.common.domain.entity.Payment;
import app.cardcapture.payment.common.domain.entity.TotalSales;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.common.dto.PaymentTokenRequestDto;
import app.cardcapture.payment.common.dto.PaymentTokenResponseDto;
import app.cardcapture.payment.common.repository.PaymentRepository;
import app.cardcapture.payment.common.repository.TotalSalesRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCommonService {

    private static final String UNVALID_PRODUCT_TOTAL_PRICE = "상품 가격의 합이 일치하지 않습니다.";
    private final PaymentRepository paymentRepository;
    private final TotalSalesRepository totalSalesRepository;
    private final RestClient restClient;
    private final PaymentConfig paymentConfig;

    @Value("${portone.api.secret}")
    private String apiSecret;

    /*public PaymentTokenResponseDto createPaymentToken() {
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
    }*/

    @Transactional
    public PaymentStartCheckResponseDto startCheck(PaymentStartCheckRequestDto paymentStartCheckRequestDto, User user) {
        String paymentId = UUID.randomUUID().toString();
        while (paymentRepository.existsByPaymentId(paymentId)) {
            paymentId = UUID.randomUUID().toString();
        }

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
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
        } // TODO: ProductType에서 가격 받아와서 다른지 확인하는걸로 바꾸기

        payment.setTotalPrice(paymentStartCheckRequestDto.totalPrice());
        payment.setRequestTime(paymentStartCheckRequestDto.requestTime());


        // 금액 xxx원 넘으면 안됨
        TotalSales totalSales = totalSalesRepository.findByIdForUpdate(1L)
                .orElseThrow(() -> new RuntimeException("TotalSales record not found"));

        long newTotalSales = totalSales.getAccumulatedSales() + payment.getTotalPrice();

        if (newTotalSales > paymentConfig.getMaxSalesAmount()) {
            throw new BusinessLogicException("이번 달 판매액을 초과하였습니다.", HttpStatus.PAYMENT_REQUIRED);
        }

        totalSales.setAccumulatedSales(newTotalSales);
        totalSalesRepository.save(totalSales);

        payment.setStatus("ARRIVED");
        Payment savedPayment = paymentRepository.save(payment);


        return PaymentStartCheckResponseDto.from(savedPayment);
    } //TODO: 구매최종완료되면 유저에게 이용권 횟수 +1 주기

    public void validateWebhook(WebhookPayload payload) {
        log.info("Received webhook:");
        log.info("Type: " + payload.type());
        log.info("Timestamp: " + payload.timestamp());
        log.info("Payment ID: " + payload.data().paymentId());
        log.info("Transaction ID: " + payload.data().transactionId());
        log.info("Total Amount: " + payload.data().totalAmount());


        // RESTCLIENT로 포트원의 결제 정보 확인 (메서드명은 checkPaymentStatusFromPortone
        // DB에 쓰기가 완료됨.
        // 포트원 에러 시 몇 번 시도 더 해봄? 해보고 안되면 DB UNCONNECTED 찍고 결제 취소 API 호출
        // API 잘못 보낸 것은 1회로 실패찍기 / IOException은 3번은 OK => 네트워크 문제로 인한 에러
        // checkPaymentStatus에서 복구 과정이 있기 때문에 IOException 횟수 줄이거나 1번만 해도 OK
    }

    public PaymentStatusResponseDto checkPaymentStatus(String paymentId) {
        // DB에 payment status가 ARRIVED면, 포트원 API로 결제 정보 확인(checkPaymentStatusFromPortone)
        // DB 업데이트 완료 후, PAID면 200, 아니면 404
        return new PaymentStatusResponseDto("PAID");
    }
}
