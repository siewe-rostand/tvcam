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
     * Calcule le montant net à payer pour une nouvelle facture
     * Dette antérieure + Montant mensuel = Net à payer
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateBillAmount(Customers customer, Integer month, Integer year) {
        // Calculer la dette antérieure (excluant la facture courante)
        BigDecimal previousDebt = getDebtAmount(customer, month, year);

        // Le montant net = dette antérieure + montant mensuel
        BigDecimal monthlyAmount = DEFAULT_MONTHLY_AMOUNT;
        BigDecimal netToPay = previousDebt.add(monthlyAmount);

        log.debug("Calcul pour client {}: Dette antérieure = {}, Montant mensuel = {}, Net à payer = {}",
                customer.getName(), previousDebt, monthlyAmount, netToPay);

        return netToPay;
    }

    /**
     * Calcule la dette totale d'un client (montants impayés des factures précédentes)
     * Dette = Somme de (net_to_pay - paid_amount) pour toutes les factures non soldées
     * Exclut la facture courante pour éviter la duplication lors de la régénération
     */
    @Transactional(readOnly = true)
    public BigDecimal getDebtAmount(Customers customer, Integer currentMonth, Integer currentYear) {
        // Récupérer toutes les factures du client
        List<Bills> allBills = billRepository.findAllByCustomers(customer);

        return allBills.stream()
                .filter(bill -> {
                    // Exclure la facture courante pour éviter la duplication lors de la régénération
                    if (currentMonth != null && currentYear != null) {
                        return !(bill.getMonth().equals(currentMonth) && bill.getYear().equals(currentYear));
                    }
                    return true;
                })
                .filter(bill -> {
                    // Ne considérer que les factures avec un solde impayé
                    BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
                    BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
                    return netToPay.subtract(paidAmount).compareTo(BigDecimal.ZERO) > 0;
                })
                .map(bill -> {
                    // Calculer le solde impayé de chaque facture
                    BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
                    BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
                    return netToPay.subtract(paidAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Version surchargée pour maintenir la compatibilité
     */
    @Transactional(readOnly = true)
    public BigDecimal getDebtAmount(Customers customer) {
        return getDebtAmount(customer, null, null);
    }

    /**
     * Calcule la dette actuelle d'une facture spécifique
     * Dette de la facture = net_to_pay - paid_amount
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateCurrentBillDebt(Bills bill) {
        BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal debt = netToPay.subtract(paidAmount);
        return debt.compareTo(BigDecimal.ZERO) > 0 ? debt : BigDecimal.ZERO;
    }

    /**
     * Met à jour les dettes de toutes les factures d'un client après génération/paiement
     * Cette méthode recalcule et met à jour le champ 'debt' dans chaque facture
     * Le champ 'debt' représente le solde restant à payer pour cette facture (net_to_pay - paid_amount)
     */
    @Transactional
    public void updateAllBillsDebt(Customers customer) {
        List<Bills> allBills = billRepository.findAllByCustomers(customer);

        for (Bills bill : allBills) {
            // Calculer le solde restant à payer pour cette facture
            BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
            BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal remainingDebt = netToPay.subtract(paidAmount);

            // La dette ne peut pas être négative
            bill.setDebt(remainingDebt.compareTo(BigDecimal.ZERO) > 0 ? remainingDebt : BigDecimal.ZERO);

            // Mettre à jour le statut de paiement
            updatePaymentStatus(bill);
        }

        billRepository.saveAll(allBills);
    }

    /**
     * Met à jour le statut de paiement d'une facture selon son solde
     */
    private void updatePaymentStatus(Bills bill) {
        BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;

        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentStatus(PaymentStatus.UNPAID);
        } else if (paidAmount.compareTo(netToPay) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }
    }

    /**
     * Vérifie si un client a des factures impayées
     */
    @Transactional(readOnly = true)
    public boolean hasUnpaidBills(Customers customer) {
        return getDebtAmount(customer).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Calcule le solde créditeur d'un client (paiements en avance)
     */
    @Transactional(readOnly = true)
    public BigDecimal getCreditBalance(Customers customer) {
        List<Bills> allBills = billRepository.findAllByCustomers(customer);

        return allBills.stream()
                .map(bill -> {
                    BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
                    BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
                    BigDecimal excess = paidAmount.subtract(netToPay);
                    return excess.compareTo(BigDecimal.ZERO) > 0 ? excess : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le montant total à payer pour un client selon sa fréquence de paiement
     */
    public BigDecimal calculateTotalAmount(Customers customer, int numberOfMonths) {
        PaymentFrequency frequency = customer.getPaymentFrequency();
        BigDecimal grossAmount = DEFAULT_MONTHLY_AMOUNT.multiply(BigDecimal.valueOf(numberOfMonths));
        BigDecimal finalAmount = applyDiscount(grossAmount, frequency);

        log.info("Calcul pour client {}: {} mois, montant brut: {}, montant final: {}",
                customer.getCustomerId(), numberOfMonths, grossAmount, finalAmount);

        return finalAmount;
    }

    /**
     * Applique le rabais selon la fréquence de paiement
     */
    private BigDecimal applyDiscount(BigDecimal originalAmount, PaymentFrequency frequency) {
        if (frequency == PaymentFrequency.MONTHLY) {
            return originalAmount;
        }

        Optional<Discount> discountOpt = discountRepository.findByPaymentFrequencyAndIsActiveTrue(frequency);
        if (discountOpt.isPresent()) {
            Discount discount = discountOpt.get();
            return discount.calculateDiscountedAmount(originalAmount);
        }

        return originalAmount;
    }

    /**
     * Récupère le pourcentage de rabais pour une fréquence donnée
     */
    public BigDecimal getDiscountPercentage(PaymentFrequency frequency) {
        Optional<Discount> discountOpt = discountRepository.findByPaymentFrequencyAndIsActiveTrue(frequency);
        return discountOpt.map(Discount::getDiscountPercentage).orElse(BigDecimal.ZERO);
    }

    /**
     * Vérifie si un client est éligible pour un rabais selon sa fréquence de paiement
     */
    public boolean isEligibleForDiscount(Customers customer) {
        return customer.getPaymentFrequency() != PaymentFrequency.MONTHLY;
    }
}
