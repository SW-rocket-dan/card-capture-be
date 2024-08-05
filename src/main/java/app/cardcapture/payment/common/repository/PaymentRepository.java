package app.cardcapture.payment.common.repository;

import app.cardcapture.payment.common.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByPaymentId(String paymentId);

    Optional<Payment> findByPaymentId(String paymentId); // TODO: User 정보도 일치해야 하는 걸로 바꾸기??
}
