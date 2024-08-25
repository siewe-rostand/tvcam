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
public class PaymentResponse {
    private Long id;
    private String paymentMethod;
    private String paymentDate;
    private BigDecimal paymentAmount;
    private String paymentStatus;
    private String paymentReference;
    private String customerName;
    private Long customerId;
    private String month;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private String user;
    private String customerPaymentFrequency;
}
