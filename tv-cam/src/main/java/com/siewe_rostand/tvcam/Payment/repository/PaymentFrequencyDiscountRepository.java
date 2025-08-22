package com.siewe_rostand.tvcam.Payment.repository;

import com.siewe_rostand.tvcam.Payment.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.model.PaymentFrequencyDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PaymentFrequencyDiscount entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface PaymentFrequencyDiscountRepository extends JpaRepository<PaymentFrequencyDiscount, Long> {
    
    Optional<PaymentFrequencyDiscount> findByPaymentFrequencyAndIsActiveTrue(PaymentFrequency paymentFrequency);
    
    List<PaymentFrequencyDiscount> findAllByIsActiveTrueOrderByMinimumMonths();
    
    boolean existsByPaymentFrequencyAndIsActiveTrue(PaymentFrequency paymentFrequency);
}