package com.siewe_rostand.tvcam.Payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

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
  private Long customerId;

  @NotNull(message = "The field userId must not be null")
  private Long userId;

  @DecimalMin(message = "must provide the payment amount", value = "0")
  private BigDecimal amount;

  private String paymentMethod;
  private String observation;
  private String customerPaymentFrequency;
}
