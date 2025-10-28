package com.siewe_rostand.tvcam.Payment.dto;

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
    private BigDecimal amount;
    private String status;
    private String reference;
    private String customerName;
    private Long customerId;
    private Integer month;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private String user;
    private String customerPaymentFrequency;
}
