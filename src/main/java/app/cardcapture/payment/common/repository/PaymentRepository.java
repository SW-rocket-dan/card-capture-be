package app.cardcapture.payment.common.repository;

import app.cardcapture.payment.common.domain.entity.Payment;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    boolean existsByPaymentId(String paymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId")
    Optional<Payment> findByPaymentIdWithLock(String paymentId);

    @Query("SELECT p FROM Payment p " +
        "WHERE p.paymentStatus = 'PENDING' " +
        "AND p.requestTime BETWEEN :startTime AND :endTime")
    List<Payment> findPendingPaymentsInRange(@Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}
