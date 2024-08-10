package app.cardcapture.payment.common.service;

import app.cardcapture.payment.common.dto.PortoneWebhookReqeustDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.embed.PaymentProduct;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.dto.ProductDto;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.config.PaymentConfig;
import app.cardcapture.payment.common.domain.entity.Payment;
import app.cardcapture.payment.common.domain.entity.TotalSales;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.common.repository.PaymentRepository;
import app.cardcapture.payment.common.repository.TotalSalesRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private static final String PAYMENT_NOT_FOUND = "결제 정보를 찾을 수 없습니다.";

    private final PaymentRepository paymentRepository;
    private final TotalSalesRepository totalSalesRepository;
    private final UserProductCategoryRepository userProductCategoryRepository;
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

        List<PaymentProduct> paymentProducts = paymentStartCheckRequestDto.products()
                .stream()
                .map(ProductDto::toEntity)
                .toList();

        payment.setPaymentProducts(paymentProducts);

        int totalPrice = paymentProducts.stream()
                .mapToInt(PaymentProduct::getTotalPrice)
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

    @Transactional
    public PaymentStatusResponseDto checkPaymentStatus(String paymentId, User user) {
        // DB에 payment status가 ARRIVED면, 포트원 API로 결제 정보 확인(checkPaymentStatusFromPortone)
        // DB 업데이트 완료 후, PAID면 200, 아니면 404
        Payment payment = paymentRepository.findByPaymentIdWithLock(paymentId) // (select for update 락 걸기)
                .orElseThrow(() -> new BusinessLogicException(PAYMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        if ("FINAL_PAID".equals(payment.getStatus())) {
            return new PaymentStatusResponseDto("FINAL_PAID"); // Exception을 띄울지?? 정상응답해도 상관없음
        }
        // 이미 이용권 반영 처리된 payment라면 아래 다 무시해야함
        // * 동시성 처리. 이 checkPaymentStatus가 분산 환경에서 여러 번 호출될 수 있으니까, Payment status에 select for update 락 걸어야함

        // payment에서 모든 productId와 quantity를 조회한다.
        // 모든 productId에 대해, 각각 아래를 실행한다.
        // 각 상품(DisplayProduct)에 맞는 ProductCategory를 조회한다.
        // 각 ProductCategory에 대해,

        List<PaymentProduct> products = payment.getPaymentProducts();
        PaymentProduct product = products.get(0);
        ProductCategory productCategory = product.getProductCategory();
        int quantity = product.getQuantity();

        // 이미 있는 값이면 업데이트해줘야 한다
        if (userProductCategoryRepository.existsByUserAndProductCategory(user, productCategory)) {
            UserProductCategory userProductCategory = userProductCategoryRepository.findByUserAndProductCategory(user, productCategory)
                    .orElseThrow(() -> new BusinessLogicException("UserProductCategory not found", HttpStatus.NOT_FOUND));
            userProductCategory.setQuantity(userProductCategory.getQuantity() + quantity);
            userProductCategoryRepository.save(userProductCategory);
        } else {
            UserProductCategory userProductCategory = new UserProductCategory();
            userProductCategory.setProductCategory(productCategory);
            userProductCategory.setQuantity(quantity);// DisplayProduct에서 여러 개의 ProductCategory를 제공할 경우, 각각의 상품에 대해 정해진 개수를 써야함. 그러면 이 quantity값이 아닌 DisplayProduct에서 가져와야함. 우선 임시로 넣어놓음
            userProductCategory.setUser(user);
            userProductCategoryRepository.save(userProductCategory);
        }
        payment.setStatus("FINAL_PAID");
        return new PaymentStatusResponseDto("FINAL_PAID");
    }

    @Transactional
    public void saveUserProductCategory(ProductCategory productCategory, int count, User user) {
        if (userProductCategoryRepository.existsByUserAndProductCategory(user, productCategory)) {
            throw new BusinessLogicException("이미 가입된 유저입니다.", HttpStatus.BAD_REQUEST);
        } else {
            UserProductCategory userProductCategory = new UserProductCategory();
            userProductCategory.setProductCategory(productCategory);
            userProductCategory.setQuantity(count);
            userProductCategory.setUser(user);
            userProductCategoryRepository.save(userProductCategory);
        }
    }
}
