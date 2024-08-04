package app.cardcapture.payment.common.repository;

import app.cardcapture.payment.common.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByPaymentId(String paymentId);
}
