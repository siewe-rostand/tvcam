package com.siewe_rostand.tvcam.Payment.services;

import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Bills.services.BillServices;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Payment.dto.PaymentMapper;
import com.siewe_rostand.tvcam.Payment.dto.PaymentRequest;
import com.siewe_rostand.tvcam.Payment.dto.PaymentResponse;
import com.siewe_rostand.tvcam.Payment.model.Payments;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentMethod;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.Payment.repository.PaymentRepository;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.Users.services.UserService;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.common.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.FORMATTER;

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
    private final BillServices billServices;
    private final CustomersRepository customersRepository;
    private final ObjectsValidator<PaymentRequest> validator;
    private final UserService userService;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Traitement du paiement pour le client ID: {}, montant: {}",
                request.getCustomerId(), request.getAmount());
        validator.validate(request);

        Users connectedUser = userService.findUserById(request.getUserId());
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(
                    "Le montant du paiement est invalide et doit être supérieur à " + request.getAmount(),
                    "Veuillez fournir un montant de paiement valide");
        }

        Customers customer = customersRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun client avec l'ID " + request.getCustomerId()
                                + " trouvé! Veuillez entrer un ID client valide"));
        List<Bills> unpaidBills = billRepository
                .findByCustomerIdAndPaymentStatusNotOrderByYearAscMonthAsc(customer.getCustomerId());


        if (unpaidBills.isEmpty()) {
            throw new ApiException("Aucune facture impayée trouvée pour le client ID: "
                    + customer.getCustomerId(),
                    "Le client n'a aucune facture en attente de paiement");
        }
        Bills currentBill = unpaidBills.get(unpaidBills.size() - 1);

        BigDecimal soldePaiement = request.getAmount();
//        Allocation du montant et mise à jour des factures ---
        Bills premiereFactureModifiee = null;
        for (Bills bill : unpaidBills) {
            // Le paiement a été entièrement alloué
            if (soldePaiement.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // Calcul du montant restant dû sur CETTE facture: NetToPay - PaidAmount
            BigDecimal balanceRemaining = bill.getNetToPay().subtract(bill.getPaidAmount());

            // Montant à appliquer à la facture (minimum entre le solde du paiement et ce qui reste à payer)
            BigDecimal appliedAmount = soldePaiement.min(balanceRemaining);

            if (appliedAmount.compareTo(BigDecimal.ZERO) > 0) {
                bill.setPaidAmount(bill.getPaidAmount().add(appliedAmount));
                // Calculer le nouvel impayé après application
                BigDecimal nouvelImpaye = balanceRemaining.subtract(appliedAmount);
                if (nouvelImpaye.compareTo(BigDecimal.ZERO) <= 0) {
                    bill.setPaymentStatus(PaymentStatus.PAID);
                } else {
                    bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
                }

                // Déduire le montant appliqué du solde du paiement reçu
                soldePaiement = soldePaiement.subtract(appliedAmount);
                if (premiereFactureModifiee == null) {
                    premiereFactureModifiee = bill;
                }
            }
        }

        for (Bills bill : unpaidBills) {
            if (bill.getPaymentStatus() == PaymentStatus.PAID) {
                continue;
            }
            if (bill.getDebt() != null) {
                bill.setDebt(bill.getDebt().subtract(request.getAmount()).max(BigDecimal.ZERO));
            }
            bill.setNetToPay(bill.getNetToPay().subtract(request.getAmount()).max(BigDecimal.ZERO));
        }

        Bills billToLinkPaymentTo = unpaidBills.stream()
                .filter(b -> b.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .orElse(unpaidBills.get(0));

        LocalDateTime now = LocalDateTime.now();

        Payments payment = new Payments();
        payment.setBills(billToLinkPaymentTo);
        payment.setPaymentDate(FORMATTER.format(now));
        payment.setPaymentRef(paymentReferenceGenerator.generatePaymentReference());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod() == null ? PaymentMethod.CASH
                : PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        payment.setObservation(request.getObservation());
        payment.setUsers(connectedUser);

        paymentRepository.save(payment);

        // Mise à jour du montant payé et du statut de la facture
        BigDecimal newPaidAmount = currentBill.getPaidAmount().add(request.getAmount());
        currentBill.setPaidAmount(newPaidAmount);

        log.info("Paiement traité avec succès. Référence: {}", payment.getPaymentRef());
        return paymentMapper.toResponse(payment);
    }

    private BigDecimal calculateRemainingAmount(Bills bill) {
        BigDecimal netToPay = bill.getNetToPay() != null ? bill.getNetToPay() : BigDecimal.ZERO;
        BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        return netToPay.subtract(paidAmount);
    }

    @Override
    public PaginatedResponse<PaymentResponse> findAll(Integer page, Integer size, String sortBy,
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
