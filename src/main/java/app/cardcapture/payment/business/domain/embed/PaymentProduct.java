package app.cardcapture.payment.business.domain.embed;

import app.cardcapture.payment.business.domain.ProductCategory;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private ProductCategory productCategory;

    @Min(1)
    private int quantity;

    @Min(1)
    private int price;

    public int getTotalPrice() {
        return quantity * price;
    }
}
