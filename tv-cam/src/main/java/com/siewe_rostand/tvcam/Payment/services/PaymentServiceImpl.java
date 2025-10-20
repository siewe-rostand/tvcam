package com.siewe_rostand.tvcam.Payment.services;

import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Bills.service.BillCalculationService;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Payment.dto.PaymentMapper;
import com.siewe_rostand.tvcam.Payment.dto.PaymentRequest;
import com.siewe_rostand.tvcam.Payment.dto.PaymentResponse;
import com.siewe_rostand.tvcam.Payment.model.Payments;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentMethod;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.Payment.repository.PaymentRepository;
import com.siewe_rostand.tvcam.common.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentReferenceGenerator paymentReferenceGenerator;
    private final BillRepository billRepository;
    private final CustomersRepository customersRepository;
    private final ObjectsValidator<PaymentRequest> validator;
    private final BillCalculationService billCalculationService;

    @Override
    @Transactional
    public PaymentResponse save(PaymentRequest paymentRequest) {
        log.info("Traitement du paiement pour le client ID: {}, montant: {}",
                paymentRequest.getCustomerId(), paymentRequest.getAmount());

        validator.validate(paymentRequest);

        if (paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(
                    "Le montant du paiement est invalide et doit être supérieur à " + paymentRequest.getAmount(),
                    "Veuillez fournir un montant de paiement valide");
        }

        Customers customer = customersRepository
                .findById(paymentRequest.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun client avec l'ID " + paymentRequest.getCustomerId()
                                + " trouvé! Veuillez entrer un ID client valide"));

        Bills currentBill = billRepository
                .findByCustomersAndCurrentPeriodBill(customer, true)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune facture avec l'ID " + paymentRequest.getBillId()
                                + " trouvée! Veuillez entrer un ID de facture valide"));

        if (currentBill.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ApiException("Tentative de paiement d'une facture déjà payée",
                    "La facture actuelle a déjà été payée");
        }

        // Vérification des rabais disponibles avec BillCalculationService
        if (billCalculationService.isEligibleForDiscount(customer)) {
            BigDecimal discountPercentage = billCalculationService
                    .getDiscountPercentage(customer.getPaymentFrequency());
            log.info("Client {} éligible au rabais de {}% pour fréquence {}",
                    customer.getCustomerId(), discountPercentage, customer.getPaymentFrequency());
        }

        BigDecimal remainingAmount = currentBill.getNetToPay().subtract(currentBill.getPaidAmount());
        log.debug("Montant restant à payer: {}", remainingAmount);

        if (paymentRequest.getAmount().compareTo(remainingAmount) > 0) {
            throw new ApiException(
                    "Le montant du paiement dépasse ce qui reste à payer. Montant restant: " + remainingAmount);
        }

        // Mise à jour de la fréquence de paiement du client si spécifiée
        if (paymentRequest.getCustomerPaymentFrequency() != null) {
            PaymentFrequency newFrequency = PaymentFrequency.valueOf(paymentRequest.getCustomerPaymentFrequency());
            customer.setPaymentFrequency(newFrequency);
            customersRepository.save(customer);
            log.info("Fréquence de paiement mise à jour pour le client {}: {}", customer.getCustomerId(), newFrequency);
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        Payments payment = new Payments();
        payment.setBills(currentBill);
        payment.setPaymentDate(formatter.format(now));
        payment.setPaymentRef(paymentReferenceGenerator.generatePaymentReference());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod() == null ? PaymentMethod.CASH
                : PaymentMethod.valueOf(paymentRequest.getPaymentMethod()));
        payment.setObservation(paymentRequest.getObservation());

        paymentRepository.save(payment);

        // Mise à jour du montant payé et du statut de la facture
        BigDecimal newPaidAmount = currentBill.getPaidAmount().add(paymentRequest.getAmount());
        currentBill.setPaidAmount(newPaidAmount);

        // Correction de la logique de statut
        if (newPaidAmount.compareTo(currentBill.getNetToPay()) >= 0) {
            currentBill.setPaymentStatus(PaymentStatus.PAID);
            log.info("Facture complètement payée pour le client {}", customer.getCustomerId());

            // Gérer l'excédent de paiement (avance)
            BigDecimal excess = newPaidAmount.subtract(currentBill.getNetToPay());
            if (excess.compareTo(BigDecimal.ZERO) > 0) {
                log.info("Excédent de paiement détecté pour le client {}: {}",
                        customer.getCustomerId(), excess);
                // L'excédent sera automatiquement pris en compte lors de la prochaine génération de facture
                // grâce à la méthode getCreditBalance() dans BillCalculationService
            }
        } else {
            currentBill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
            log.info("Facture partiellement payée pour le client {}: {}/{}",
                    customer.getCustomerId(), newPaidAmount, currentBill.getNetToPay());
        }

        // Recalculer la dette de la facture courante
        BigDecimal currentDebt = billCalculationService.calculateCurrentBillDebt(currentBill);
        currentBill.setDebt(currentDebt);

        billRepository.save(currentBill);

        // Mettre à jour toutes les dettes du client après le paiement
        billCalculationService.updateAllBillsDebt(customer);

        log.info("Paiement traité avec succès. Référence: {}", payment.getPaymentRef());
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy,
            String direction, String name) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Payments> payments;
        if (!name.isEmpty()) {
            payments = paymentRepository.findAll(name, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }
        return buildResponse(payments, pageable);
    }

    @Override
    public List<PaymentResponse> findPaymentByCustomerId(Long customerId) {
        Customers customer = customersRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException(
                "Aucun client avec l'ID " + customerId + " trouvé! Veuillez entrer un ID client valide"));

        List<Payments> payments = paymentRepository.findByBills_CustomersCustomerId(customer.getCustomerId());
        List<PaymentResponse> paymentResponses = new ArrayList<>();
        for (Payments payment : payments) {
            paymentResponses.add(paymentMapper.toResponse(payment));
        }
        return paymentResponses;
    }

    @Override
    public List<PaymentResponse> findByBills_Month(Integer month) {
        List<Payments> payments = paymentRepository.findByBills_Month(month);
        List<PaymentResponse> paymentResponses = new ArrayList<>();
        for (Payments payment : payments) {
            paymentResponses.add(paymentMapper.toResponse(payment));
        }
        return paymentResponses;
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
    }

    private PaginatedResponse<PaymentResponse> buildResponse(Page<Payments> payments, Pageable pageable) {
        Page<PaymentResponse> responses = payments
                .map((Function<? super Payments, ? extends PaymentResponse>) paymentMapper::toResponse);
        return PaginatedResponse.<PaymentResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK).statusCode(HttpStatus.OK.value())
                .message("Paiements récupérés avec succès")
                .data(responses.getContent())
                .lastPage(responses.isLast()).firstPage(responses.isFirst())
                .totalPages(responses.getTotalPages()).totalElements(responses.getNumberOfElements())
                .empty(responses.isEmpty()).sorted(responses.getSort().isSorted())
                .numberOfElements(responses.getNumberOfElements())
                .paged(pageable.isPaged()).page(pageable.getPageNumber())
                .build();
    }
}
