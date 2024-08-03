package app.cardcapture.payment.common.domain.entity;

import app.cardcapture.payment.business.domain.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    private String id;

    @ElementCollection
    @Valid
    @NotNull
    private List<Product> products;

    @Min(1)
    int totalPrice;

    @Column(nullable = false)
    private LocalDateTime requestTime;

}
