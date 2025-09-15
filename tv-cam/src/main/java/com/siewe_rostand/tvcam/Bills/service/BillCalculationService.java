package com.siewe_rostand.tvcam.Bills.service;

import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Discount.model.Discount;
import com.siewe_rostand.tvcam.Discount.repository.DiscountRepository;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DEFAULT_MONTHLY_AMOUNT;

/**
 * Service pour calculer les montants de facturation avec rabais
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillCalculationService {

    private final DiscountRepository discountRepository;
    private final BillRepository billRepository;


    /**
     * Calcule le montant total à payer pour un client selon sa fréquence de
     * paiement
     */
    public BigDecimal calculateTotalAmount(Customers customer, int numberOfMonths) {
        PaymentFrequency frequency = customer.getPaymentFrequency();

        // Calcul du montant brut (nombre de mois × montant mensuel)
        BigDecimal grossAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(numberOfMonths));

        // Application du rabais selon la fréquence
        BigDecimal finalAmount = applyDiscount(grossAmount, frequency);

        log.info("Calcul pour client {}: {} mois, montant brut: {}, montant final: {}",
                customer.getCustomerId(), numberOfMonths, grossAmount, finalAmount);

        return finalAmount;
    }

    /**
     * Calcule le montant pour un paiement trimestriel (3 mois)
     */
    public BigDecimal calculateQuarterlyAmount() {
        return calculateAmountWithDiscount(3, PaymentFrequency.QUARTERLY);
    }

    /**
     * Calcule le montant pour un paiement semestriel (6 mois)
     */
    public BigDecimal calculateSemiAnnualAmount() {
        return calculateAmountWithDiscount(6, PaymentFrequency.SEMI_ANNUALLY);
    }

    /**
     * Calcule le montant pour un paiement annuel (12 mois)
     */
    public BigDecimal calculateAnnualAmount() {
        return calculateAmountWithDiscount(12, PaymentFrequency.ANNUALLY);
    }

    /**
     * Applique le rabais selon la fréquence de paiement
     */
    private BigDecimal applyDiscount(BigDecimal originalAmount, PaymentFrequency frequency) {
        if (frequency == PaymentFrequency.MONTHLY) {
            return originalAmount; // Pas de rabais pour le paiement mensuel
        }

        Optional<Discount> discountOpt = discountRepository.findByPaymentFrequencyAndIsActiveTrue(frequency);

        if (discountOpt.isPresent()) {
            Discount discount = discountOpt.get();
            return discount.calculateDiscountedAmount(originalAmount);
        }

        return originalAmount;
    }

    /**
     * Calcule le montant avec rabais pour un nombre de mois et une fréquence donnés
     */
    private BigDecimal calculateAmountWithDiscount(int numberOfMonths, PaymentFrequency frequency) {
        BigDecimal grossAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(numberOfMonths));
        return applyDiscount(grossAmount, frequency);
    }

    /**
     * Calcule le montant d'économie réalisé avec un paiement avancé
     */
    public BigDecimal calculateSavings(PaymentFrequency frequency, int numberOfMonths) {
        BigDecimal grossAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(numberOfMonths));
        BigDecimal discountedAmount = applyDiscount(grossAmount, frequency);
        return grossAmount.subtract(discountedAmount);
    }

    /**
     * Vérifie si un client est éligible pour un rabais
     */
    public boolean isEligibleForDiscount(Customers customer) {
        return customer.getPaymentFrequency() != PaymentFrequency.MONTHLY;
    }

    /**
     * Récupère le pourcentage de rabais pour une fréquence donnée
     */
    public BigDecimal getDiscountPercentage(PaymentFrequency frequency) {
        Optional<Discount> discountOpt = discountRepository.findByPaymentFrequencyAndIsActiveTrue(frequency);
        return discountOpt.map(Discount::getDiscountPercentage).orElse(BigDecimal.ZERO);
    }

    /**
     * Calcule le montant d'une facture en tenant compte des arriérés et des rabais
     */
    public BigDecimal calculateBillAmount(Customers customer, BigDecimal debt) {
        PaymentFrequency frequency = customer.getPaymentFrequency();

        // Pour les paiements mensuels
        if (frequency == PaymentFrequency.MONTHLY) {
            return DEFAULT_MONTHLY_AMOUNT.add(debt != null ? debt : BigDecimal.ZERO);
        }

        // Pour les autres fréquences, calculer selon la période
        int months = getMonthsForFrequency(frequency);
        BigDecimal totalAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(months));

        return totalAmount.add(debt != null ? debt : BigDecimal.ZERO);
    }

    /**
     * Retourne le nombre de mois selon la fréquence
     */
    private int getMonthsForFrequency(PaymentFrequency frequency) {
        return switch (frequency) {
            case QUARTERLY -> 3;
            case SEMI_ANNUALLY -> 6;
            case ANNUALLY -> 12;
            default -> 1;
        };
    }

    @Transactional(readOnly = true)
    public BigDecimal getUnpaidAmount(Customers customer) {
        List<Bills> unpaidBills = billRepository.findAllByCustomersAndPaymentStatus(customer, PaymentStatus.UNPAID);
        return unpaidBills.stream()
                .map(Bills::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Transactional(readOnly = true)
    public BigDecimal calculateBillAmount(Customers customer) {
        // Utiliser BillCalculationService pour les calculs avec rabais
        BigDecimal debt = getUnpaidAmount(customer);
        return calculateBillAmount(customer, debt);
    }


    /**
     * Génère un résumé de facturation avec les détails du rabais
     */
    public BillSummary generateBillSummary(Customers customer, BigDecimal debt) {
        PaymentFrequency frequency = customer.getPaymentFrequency();
        int months = getMonthsForFrequency(frequency);
        BigDecimal grossAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(months));
        BigDecimal discount = calculateSavings(frequency, months);
        BigDecimal netAmount = grossAmount.subtract(discount);
        BigDecimal totalWithDebt = netAmount.add(debt != null ? debt : BigDecimal.ZERO);

        return BillSummary.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getName())
                .paymentFrequency(frequency)
                .numberOfMonths(months)
                .grossAmount(grossAmount)
                .discountAmount(discount)
                .netAmount(netAmount)
                .debt(debt)
                .totalAmount(totalWithDebt)
                .discountPercentage(getDiscountPercentage(frequency))
                .build();
    }

    /**
     * Classe interne pour le résumé de facturation
     */
    @lombok.Builder
    @lombok.Data
    public static class BillSummary {
        private Long customerId;
        private String customerName;
        private PaymentFrequency paymentFrequency;
        private Integer numberOfMonths;
        private BigDecimal grossAmount;
        private BigDecimal discountAmount;
        private BigDecimal netAmount;
        private BigDecimal debt;
        private BigDecimal totalAmount;
        private BigDecimal discountPercentage;
    }
}
