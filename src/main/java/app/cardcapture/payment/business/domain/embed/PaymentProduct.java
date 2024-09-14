package app.cardcapture.payment.business.domain.embed;

import app.cardcapture.payment.business.domain.ProductCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProduct {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory productCategory;

    @Min(1)
    private int quantity;

    @Min(1)
    private int price;

    public int getTotalPrice() {
        return quantity * price;
    }
}
