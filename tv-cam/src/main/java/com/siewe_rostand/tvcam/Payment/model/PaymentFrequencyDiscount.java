package com.siewe_rostand.tvcam.Payment.model;

import com.siewe_rostand.tvcam.Payment.PaymentFrequency;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * PaymentFrequencyDiscount entity representing discount rates for different payment frequencies
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "payment_frequency_discounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentFrequencyDiscount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long discountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency", nullable = false, unique = true)
    private PaymentFrequency paymentFrequency;

    @DecimalMin(value = "0.0", message = "Discount percentage must be positive")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100%")
    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Min(value = 1, message = "Minimum months must be at least 1")
    @Column(name = "minimum_months", nullable = false)
    private Integer minimumMonths;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Calculate the discounted amount based on the original amount
     */
    public BigDecimal calculateDiscountedAmount(BigDecimal originalAmount) {
        if (originalAmount == null || discountPercentage == null) {
            return originalAmount;
        }
        
        BigDecimal discountAmount = originalAmount.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        
        return originalAmount.subtract(discountAmount);
    }

    /**
     * Calculate the total amount for the payment frequency period
     */
    public BigDecimal calculateTotalAmount(BigDecimal monthlyAmount) {
        if (monthlyAmount == null || minimumMonths == null) {
            return monthlyAmount;
        }
        
        BigDecimal totalAmount = monthlyAmount.multiply(new BigDecimal(minimumMonths));
        return calculateDiscountedAmount(totalAmount);
    }
}