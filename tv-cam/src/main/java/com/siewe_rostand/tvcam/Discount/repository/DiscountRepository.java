package com.siewe_rostand.tvcam.Discount.repository;

import com.siewe_rostand.tvcam.Discount.model.Discount;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Discount
 * 
 * @author rostand
 * @project tv-cam
 */
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByPaymentFrequencyAndIsActiveTrue(PaymentFrequency paymentFrequency);

    List<Discount> findByIsActiveTrue();

    List<Discount> findByPaymentFrequency(PaymentFrequency paymentFrequency);
}
