package com.siewe_rostand.tvcam.Discount.service;

import com.siewe_rostand.tvcam.Discount.dto.DiscountRequest;
import com.siewe_rostand.tvcam.Discount.dto.DiscountResponse;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.shared.HttpResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface du service Discount
 * 
 * @author rostand
 * @project tv-cam
 */
public interface DiscountService {

    DiscountResponse createDiscount(DiscountRequest request);

    DiscountResponse updateDiscount(Long discountId, DiscountRequest request);

    HttpResponse<Object> deleteDiscount(Long discountId);

    DiscountResponse getDiscountById(Long discountId);

    List<DiscountResponse> getAllActiveDiscounts();

    DiscountResponse getDiscountByPaymentFrequency(PaymentFrequency paymentFrequency);

    /**
     * Calcule le montant final après application des rabais pour une fréquence
     * donnée
     */
    BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, PaymentFrequency paymentFrequency);

    /**
     * Calcule le montant de rabais pour une fréquence donnée
     */
    BigDecimal calculateDiscountAmount(BigDecimal originalAmount, PaymentFrequency paymentFrequency);
}
