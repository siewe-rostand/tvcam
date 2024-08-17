package com.siewe_rostand.tvcam.Payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull(message = "the customer id must not be null")
    Long customerId;
    @NotNull(message = "the bill id must be provided")
    Long billId;
    @DecimalMin(message = "must provide the payment amount",value = "0")
    BigDecimal amount;
    String paymentMethod;
    String observation;
    String customerPaymentFrequency;
}
