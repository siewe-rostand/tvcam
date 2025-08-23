package com.siewe_rostand.tvcam.Discount.dto;

import com.siewe_rostand.tvcam.Discount.model.Discount;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Discount et DTOs
 * 
 * @author rostand
 * @project tv-cam
 */
@Component
public class DiscountMapper {

    public Discount toDiscount(DiscountRequest request) {
        if (request == null) {
            return null;
        }

        return Discount.builder()
                .paymentFrequency(request.getPaymentFrequency())
                .discountPercentage(request.getDiscountPercentage())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .minimumAmount(request.getMinimumAmount())
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .build();
    }

    public DiscountResponse toResponse(Discount discount) {
        if (discount == null) {
            return null;
        }

        return DiscountResponse.builder()
                .discountId(discount.getDiscountId())
                .paymentFrequency(discount.getPaymentFrequency())
                .discountPercentage(discount.getDiscountPercentage())
                .description(discount.getDescription())
                .isActive(discount.getIsActive())
                .minimumAmount(discount.getMinimumAmount())
                .maximumDiscountAmount(discount.getMaximumDiscountAmount())
                .createdAt(discount.getCreatedAt() != null ? discount.getCreatedAt().toString() : null)
                .updatedAt(discount.getUpdatedAt() != null ? discount.getUpdatedAt().toString() : null)
                .build();
    }

    public void updateDiscountFromRequest(DiscountRequest request, Discount discount) {
        if (request == null || discount == null) {
            return;
        }

        if (request.getPaymentFrequency() != null) {
            discount.setPaymentFrequency(request.getPaymentFrequency());
        }
        if (request.getDiscountPercentage() != null) {
            discount.setDiscountPercentage(request.getDiscountPercentage());
        }
        if (request.getDescription() != null) {
            discount.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            discount.setIsActive(request.getIsActive());
        }
        if (request.getMinimumAmount() != null) {
            discount.setMinimumAmount(request.getMinimumAmount());
        }
        if (request.getMaximumDiscountAmount() != null) {
            discount.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        }
    }
}
