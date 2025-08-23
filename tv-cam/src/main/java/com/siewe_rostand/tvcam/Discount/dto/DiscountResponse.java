package com.siewe_rostand.tvcam.Discount.dto;

import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO pour les réponses Discount
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountResponse {

    private Long discountId;
    private PaymentFrequency paymentFrequency;
    private BigDecimal discountPercentage;
    private String description;
    private Boolean isActive;
    private BigDecimal minimumAmount;
    private BigDecimal maximumDiscountAmount;
    private String createdAt;
    private String updatedAt;
}
