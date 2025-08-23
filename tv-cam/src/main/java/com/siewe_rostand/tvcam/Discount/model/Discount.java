package com.siewe_rostand.tvcam.Discount.model;

import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entité Discount pour gérer les rabais sur les paiements avancés
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "discounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Discount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long discountId;

    @NotNull(message = "La fréquence de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency", nullable = false)
    private PaymentFrequency paymentFrequency;

    @NotNull(message = "Le pourcentage de rabais est obligatoire")
    @DecimalMin(value = "0.0", message = "Le rabais ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le rabais ne peut pas dépasser 100%")
    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "minimum_amount")
    private BigDecimal minimumAmount;

    @Column(name = "maximum_discount_amount")
    private BigDecimal maximumDiscountAmount;

    /**
     * Calcule le montant de rabais pour un montant donné
     */
    public BigDecimal calculateDiscount(BigDecimal originalAmount) {
        if (!isActive || originalAmount == null) {
            return BigDecimal.ZERO;
        }

        if (minimumAmount != null && originalAmount.compareTo(minimumAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = originalAmount.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

        if (maximumDiscountAmount != null && discountAmount.compareTo(maximumDiscountAmount) > 0) {
            return maximumDiscountAmount;
        }

        return discountAmount;
    }

    /**
     * Calcule le montant final après application du rabais
     */
    public BigDecimal calculateDiscountedAmount(BigDecimal originalAmount) {
        BigDecimal discount = calculateDiscount(originalAmount);
        return originalAmount.subtract(discount);
    }
}
