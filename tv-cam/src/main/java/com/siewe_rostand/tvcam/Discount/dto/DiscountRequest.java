package com.siewe_rostand.tvcam.Discount.dto;

import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO pour les requêtes Discount
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountRequest {

    @NotNull(message = "La fréquence de paiement est obligatoire")
    private PaymentFrequency paymentFrequency;

    @NotNull(message = "Le pourcentage de rabais est obligatoire")
    @DecimalMin(value = "0.0", message = "Le rabais ne peut pas être négatif")
    @DecimalMax(value = "100.0", message = "Le rabais ne peut pas dépasser 100%")
    private BigDecimal discountPercentage;

    private String description;

    private Boolean isActive;

    private BigDecimal minimumAmount;

    private BigDecimal maximumDiscountAmount;
}
