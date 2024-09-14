package app.cardcapture.payment.common.service;

import app.cardcapture.payment.portone.dto.PortoneWebhookReqeustDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.embed.PaymentProduct;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.dto.ProductDto;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.config.PaymentConfig;
import app.cardcapture.payment.portone.config.PortoneConfig;
import app.cardcapture.payment.common.domain.PaymentStatus;
import app.cardcapture.payment.common.domain.entity.Payment;
import app.cardcapture.payment.common.domain.entity.TotalSales;
import app.cardcapture.payment.common.dto.PaymentStartCheckRequestDto;
import app.cardcapture.payment.common.dto.PaymentStartCheckResponseDto;
import app.cardcapture.payment.common.dto.PaymentStatusResponseDto;
import app.cardcapture.payment.portone.dto.PortonePaymentInquiryResponseDto;
import app.cardcapture.payment.common.repository.PaymentRepository;
import app.cardcapture.payment.common.repository.TotalSalesRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static app.cardcapture.payment.common.domain.PaymentStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCommonService {
    // TODO: 세부 예외를 만들어놓고, 이를 호출하는 곳에서 에러메세지 정하는 방식으로 빼주기
    // 예외가 너무 많아지니까, NOT_FOUND같은 건 common_not_found
    private static final String UNVALID_PRODUCT_TOTAL_PRICE = "상품 가격의 합이 일치하지 않습니다.";
    private static final String PAYMENT_NOT_FOUND = "결제 정보를 찾을 수 없습니다.";

    private final PaymentRepository paymentRepository;
    private final TotalSalesRepository totalSalesRepository;
    private final UserProductCategoryRepository userProductCategoryRepository;
    private final RestClient restClient;
    private final PaymentConfig paymentConfig;
    private final PortoneConfig portoneConfig;

    // TODO: 한 사용자가 n초 내에 얼마 이상 구매요청 못하게 limit 있어야함

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

        payment.setPaymentStatus(ARRIVED);
        Payment savedPayment = paymentRepository.save(payment);


        return PaymentStartCheckResponseDto.from(savedPayment);
    }

    @Transactional
    public void validateWebhook(PortoneWebhookReqeustDto payload) {
        log.info("validateWebhook payload: " + payload.toString());
        // 여기있는 정보는 믿을 수 없음

        // RESTCLIENT로 포트원의 결제 정보 확인 (메서드명은 checkPaymentStatusFromPortone
        // DB에 쓰기가 완료됨.
        // 포트원 에러 시 몇 번 시도 더 해봄? 해보고 안되면 DB UNCONNECTED 찍고 결제 취소 API 호출
        // API 잘못 보낸 것은 1회로 실패찍기 / IOException은 3번은 OK => 네트워크 문제로 인한 에러
        // checkPaymentStatus에서 복구 과정이 있기 때문에 IOException 횟수 줄이거나 1번만 해도 OK

        checkAndAddProductCategoryWithPortone(payload.data().paymentId());
    }


    @Transactional
    public PaymentStatusResponseDto checkPaymentStatus(String paymentId, User user) {
        // DB에 payment status가 ARRIVED면, 포트원 API로 결제 정보 확인(checkPaymentStatusFromPortone)
        // DB 업데이트 완료 후, PAID면 200, 아니면 404

        // 이미 이용권 반영 처리된 payment라면 아래 다 무시해야함
        // * 동시성 처리. 이 checkPaymentStatus가 분산 환경에서 여러 번 호출될 수 있으니까, Payment status에 select for update 락 걸어야함

        Payment savedPayment = checkAndAddProductCategoryWithPortone(paymentId);
        System.out.println("savedPayment = " + savedPayment.getPaymentStatus());
        return new PaymentStatusResponseDto(savedPayment.getPaymentStatus());
    } // TODO: 리팩터링할 때, 정석적인 방법으로는 테스트코드를 먼저 짜놓고, 이게 깨지지 않는지 확인하면서 하는것이 맞다
    // 하지만 애매한 부분은, 아래에서 restClient직접호출하는 부분은 portoneService로 빼줄 수도 있다.
    // 여기서 그러면 테스트코드를 StickerService, PortoneService 둘 다 계속 바꿔야하기때문에 이번에는 리팩터링먼저하고 짜기

    @Transactional
    public Payment checkAndAddProductCategoryWithPortone(String unchekcedPaymentId) {
        log.info("checkAndAddProductCategoryWithPortone unchekcedPaymentId: " + unchekcedPaymentId);
        PortonePaymentInquiryResponseDto portonePaymentInquiryResponseDto = restClient.get()
                .uri("https://api.portone.io/payments/" + unchekcedPaymentId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", portoneConfig.getApiSecret())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    log.info("Portone API error: " + response.getStatusCode());
                    canclePayment(unchekcedPaymentId);
                    throw new BusinessLogicException( //TODO: 일단 바로 취소되게 해놨는데 ,나중에 여러 번 시도하는 걸로 바꾸기
                            "결제 확인에 실패했습니다. 만약 결제된 내역이 있으면, 취소됩니다. 잠시 후 다시 결제해주시고, 계속 결제가 실패한다면 고객센터로 문의주세요.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .body(PortonePaymentInquiryResponseDto.class);

        log.info("portonePaymentInquiryResponseDto = " + portonePaymentInquiryResponseDto.toString());

        // 여기 있는 정보부터 믿을 수 있음
        PaymentStatus paymentStatus = PaymentStatus.valueOf(portonePaymentInquiryResponseDto.status());
        String paymentId = portonePaymentInquiryResponseDto.id();
        int totalPrice = portonePaymentInquiryResponseDto.amount().total();
        String currency = portonePaymentInquiryResponseDto.currency();

        Payment payment = paymentRepository.findByPaymentIdWithLock(paymentId) // (select for update 락 걸기)
                .orElseThrow(() -> new BusinessLogicException(PAYMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (totalPrice != payment.getTotalPrice()) {
            canclePayment(payment);
            throw new BusinessLogicException("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if (!currency.equals("KRW")) {
            canclePayment(payment);
            throw new BusinessLogicException("결제 통화가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // TODO: 각 경우에 맞게 다시 설계해야함 안전하게 모든 경우의 수 다 적는것도 방법?
        // FINAL_PAID와 PAID는 변동이 없음
        if (FINAL_PAID.equals(payment.getPaymentStatus()) && PAID.equals(paymentStatus)) {
            log.info("이미 PAID 상태입니다.");
            return payment;
        }


        
        payment.setPaymentStatus(paymentStatus);

        User user = payment.getUser();

        if (PAID.equals(paymentStatus)) { //PAID 됐을때만 +1
            return addProductCategoryQuantity(user, payment);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public void canclePayment(Payment payment) {
        restClient.post()
                .uri("https://api.portone.io/payments/" + payment.getPaymentId() + "/cancel")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", portoneConfig.getApiSecret())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    // TODO: 관리자 알람을 주든지 따로 모아놓고 배치를 주든지 해야함
                    throw new BusinessLogicException("결제 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @Transactional
    public void canclePayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentIdWithLock(paymentId)
                .orElseThrow(() -> new BusinessLogicException(PAYMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        canclePayment(payment);
    }

    @Transactional
    public Payment addProductCategoryQuantity(User user, Payment payment) {
        // payment에서 모든 productId와 quantity를 조회한다.
        // 모든 productId에 대해, 각각 아래를 실행한다.
        // 각 상품(DisplayProduct)에 맞는 ProductCategory를 조회한다.
        // 각 ProductCategory에 대해,
        if (payment.isVoucherIssued()) {
            log.info("이미 이용권이 발급된 payment입니다.");
            return payment; // TODO: 이름 Voucher로 통일하기
        }
        List<PaymentProduct> products = payment.getPaymentProducts();
        PaymentProduct product = products.get(0);
        ProductCategory productCategory = product.getProductCategory();
        int quantity = product.getQuantity();

        // 이미 있는 값이면 업데이트해줘야 한다
        if (userProductCategoryRepository.existsByUserAndProductCategory(user, productCategory)) {
            UserProductCategory userProductCategory = userProductCategoryRepository.findByUserAndProductCategoryWithLock(user, productCategory)
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
        payment.setPaymentStatus(FINAL_PAID);
        payment.setVoucherIssued(true);
        log.info("이용권이 발급되었습니다."+payment.getPaymentStatus()+payment.isVoucherIssued());

        return paymentRepository.save(payment);
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
