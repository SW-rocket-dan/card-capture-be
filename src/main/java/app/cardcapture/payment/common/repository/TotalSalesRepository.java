package app.cardcapture.payment.common.repository;

import app.cardcapture.payment.common.domain.entity.TotalSales;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TotalSalesRepository extends JpaRepository<TotalSales, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TotalSales ts WHERE ts.id = :id")
    Optional<TotalSales> findByIdForUpdate(@Param("id") Long id);
}
