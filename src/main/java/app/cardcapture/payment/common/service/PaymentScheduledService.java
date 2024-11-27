package app.cardcapture.payment.common.service;

import app.cardcapture.payment.common.domain.PaymentStatus;
import app.cardcapture.payment.common.domain.entity.Payment;
import app.cardcapture.payment.common.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduledService {

    private final PaymentRepository paymentRepository;
    private final PaymentCommonService paymentCommonService;

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "clearTemporaryCart", lockAtMostFor = "4m", lockAtLeastFor = "4m")
    public void clearTemporaryCart() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(16);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(15);

        List<Payment> paymentIdsToCancel = paymentRepository.findPendingPaymentsInRange(startTime,
            endTime);

        if (paymentIdsToCancel.isEmpty()) {
            return;
        }

        for (Payment payment : paymentIdsToCancel) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        }

        paymentRepository.saveAll(paymentIdsToCancel);

        for (Payment payment : paymentIdsToCancel) {
            paymentCommonService.canclePayment(payment);
        }
    }
}
