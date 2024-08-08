package com.siewe_rostand.tvcam.Payment;

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
    Long customerId;
    Long billId;
    BigDecimal amount;
    String paymentMethod;
    String observation;
    String customerPaymentFrequency;
}
