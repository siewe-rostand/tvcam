package com.siewe_rostand.tvcam.Bills.services;

import com.siewe_rostand.tvcam.Bills.dto.BillMapper;
import com.siewe_rostand.tvcam.Bills.dto.BillRequest;
import com.siewe_rostand.tvcam.Bills.dto.BillResponse;
import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Bills.service.BillCalculationService;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Customers.services.CustomerService;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.shared.Exceptions.OperationNotPermittedException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DATE_FORMAT;
import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.DEFAULT_MONTHLY_AMOUNT;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
@RequiredArgsConstructor
public class BillServicesImpl implements BillServices {
    private static final Logger log = LoggerFactory.getLogger(BillServicesImpl.class);
    private final BillRepository billRepository;
    private final BillCalculationService billCalculationService;
    private final ObjectsValidator<BillRequest> validator;
    private final BillMapper billMapper;
    private final CustomerService customerService;
    private final CustomersRepository customersRepository;


    @Override
    public Bills update(BillRequest request) {
        return null;
    }

    @Override
    public PaginatedResponse<BillResponse> findAll(Integer page, Integer size, String sortBy, String direction, String name) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Bills> bills;
        if (!name.isEmpty()) {
            bills = billRepository.findAll(name, pageable);
        } else {
            bills = billRepository.findAll(pageable);
        }
        return buildResponse(bills, pageable);
    }

    @Override
    public HttpResponse<List<BillResponse>> findCustomerBills(Long customerId) {
        Customers customer = customerService.getById(customerId);

        List<Bills> bills = billRepository.findAllByCustomers(customer);
        List<BillResponse> billResponses = new ArrayList<>();
        for (Bills bill : bills) {
            BillResponse billResponse = billMapper.toResponse(bill);
            billResponses.add(billResponse);
        }

        return HttpResponse.<List<BillResponse>>builder().success(true).timestamp(now())
                .data(billResponses).status(OK.getReasonPhrase())
                .message("Bills for customer " + customer.getName() + " gotten successfully").statusCode(OK.value())
                .build();
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
    }

    private PaginatedResponse<BillResponse> buildResponse(Page<Bills> bills, Pageable pageable) {
        Page<BillResponse> responses = bills.map(billMapper::toResponse);
        return PaginatedResponse.<BillResponse>builder()
                .timestamp(now())
                .status(OK).statusCode(OK.value())
                .data(responses.getContent())
                .message("Toutes les factures des clients ont été récupérées avec succès")
                .lastPage(responses.isLast()).firstPage(responses.isFirst())
                .totalPages(responses.getTotalPages()).totalElements(responses.getNumberOfElements())
                .empty(responses.isEmpty()).sorted(pageable.getSort().isSorted())
                .numberOfElements(responses.getNumberOfElements())
                .page(pageable.getPageNumber()).paged(pageable.isPaged())
                .build();
    }

    @Override
    public HttpResponse<BillResponse> delete(Long id) {
        Bills bills = billRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune facture avec cet identifiant " + id
                        + "n'a été trouvée ! Veuillez saisir un numéro de facture valide"));
        billRepository.delete(bills);
        return HttpResponse.<BillResponse>builder().timestamp(now()).success(true)
                .message("Facture supprimée avec succès").statusCode(OK.value()).status(OK.getReasonPhrase())
                .build();
    }

    @Transactional
    @Override
    public List<BillResponse> generateBillsForSelectedCustomers(List<Long> customerIds, Boolean shouldGenerate) {
        log.debug("bill request: {}", customerIds);
        if (customerIds.isEmpty()) {
            throw new OperationNotPermittedException("Aucun client sélectionné pour la génération de factures");
        }
        List<Customers> customers = customerService.getAllCustomersByIds(customerIds);

        List<BillResponse> generatedBills = new ArrayList<>();
        LocalDateTime today = now();

        for (Customers c : customers) {
            if (shouldGenerateBill(c, today) || shouldGenerate) {
                Bills savedBill = billRepository.findByCustomersAndMonthAndYear(c, today.getMonthValue(), today.getYear());
                LocalDateTime billingDate = savedBill.getDepositDate() != null ?
                        LocalDate.parse(savedBill.getDepositDate(), DATE_FORMAT).atStartOfDay() : today;
                BillRequest dynamicRequest = createBillRequestFromCustomer(c, billingDate);
                BillResponse response = generateBillForCustomer(c, billingDate, dynamicRequest);
                generatedBills.add(response);
            }
        }
        return generatedBills;
    }

    public BillResponse generateBillForCustomer(Customers customer, LocalDateTime billingDate, BillRequest request) {
        validator.validate(request);

        Bills savedBill = createOrUpdateBill(customer, request, billingDate);
        customer.setLastBillGenerationDate(billingDate);
        customersRepository.save(customer);
        return billMapper.toResponse(savedBill);
    }

    @Override
    public HttpResponse<BillResponse> generateCustomerBill(Long customerId, Boolean shouldGenerate) {
        Customers customer = customerService.getById(customerId);
        LocalDateTime today = now();

        if (shouldGenerateBill(customer, today) || shouldGenerate) {
            BillRequest dynamicRequest = createBillRequestFromCustomer(customer, today);
            BillResponse response = generateBillForCustomer(customer, today, dynamicRequest);

            return HttpResponse.<BillResponse>builder().success(true).timestamp(now())
                    .data(response).status(OK.getReasonPhrase())
                    .message("Facture générée avec succès pour le client " + customer.getName())
                    .statusCode(OK.value())
                    .build();
        } else {
            throw new OperationNotPermittedException("La génération de la facture n'est pas autorisée pour le client "
                    + customer.getName() + " en raison de la fréquence de paiement ou d'une facture existante.");
        }
    }

    /**
     * Détermine si une nouvelle facture doit être générée pour un client.
     *
     * @param customer L'objet Customer contenant les informations sur le client.
     * @param today    La date et l'heure actuelles.
     * @return Vrai si une facture doit être générée, sinon faux.
     */
    //TODO: on ne doit pas etre capable de generer la facture d'un client au dela de la date d'enregistrement au systeme
    private boolean shouldGenerateBill(Customers customer, LocalDateTime today) {
        // Si le client n'a jamais été facturé, une facture doit être générée.
        if (customer.getLastBillGenerationDate() == null) {
            return true;
        }

        // Calcule le nombre de mois écoulés depuis la dernière facture.
        long monthsSinceLastBill = ChronoUnit.MONTHS.between(customer.getLastBillGenerationDate(), today);

        // Récupère la fréquence de paiement du client.
        PaymentFrequency frequency = customer.getPaymentFrequency();

        // Utilise une expression 'switch' pour vérifier si une nouvelle facture est due
        // en fonction de la fréquence de paiement et du nombre de mois écoulés.
        return switch (frequency) {
            case MONTHLY -> monthsSinceLastBill >= 1;
            case QUARTERLY -> monthsSinceLastBill >= 3;
            case SEMI_ANNUALLY -> monthsSinceLastBill >= 6;
            case ANNUALLY -> monthsSinceLastBill >= 12;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getBillsForPrint(List<Long> billIds) {
        log.info("Récupération de {} factures pour impression", billIds.size());

        List<Bills> bills = billRepository.findAllById(billIds);

        if (bills.isEmpty()) {
            log.warn("Aucune facture trouvée pour les IDs fournis: {}", billIds);
            return new ArrayList<>();
        }

        List<BillResponse> billResponses = new ArrayList<>();
        for (Bills bill : bills) {
            BillResponse response = billMapper.toResponse(bill);
            billResponses.add(response);
        }

        log.info("{} factures récupérées avec succès pour impression", billResponses.size());
        return billResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkExistingBillsForMonth(List<Long> customerIds, Integer month, Integer year) {
        log.info("Vérification de l''existence de factures pour {} clients, mois: {}, année: {}",
                customerIds.size(), month, year);

        if (customerIds.isEmpty()) {
            return false;
        }

        List<Customers> customers = customerService.getAllCustomersByIds(customerIds);

        for (Customers customer : customers) {
            List<Bills> existingBills = billRepository.findAllByCustomersAndMonthAndYear(customer, month, year);
            if (!existingBills.isEmpty()) {
                log.info("Factures existantes trouvées pour le client {} en {} {}",
                        customer.getName(), month, year);
                return true;
            }
        }

        log.info("Aucune facture existante trouvée pour le mois {} {}", month, year);
        return false;
    }

    @Override
    @Transactional
    public int deleteBillsBatch(List<Long> billIds) {
        log.info("Suppression en lot de {} factures", billIds.size());

        if (billIds.isEmpty()) {
            return 0;
        }

        List<Bills> billsToDelete = billRepository.findAllById(billIds);

        if (billsToDelete.isEmpty()) {
            log.warn("Aucune facture trouvée pour suppression avec les IDs: {}", billIds);
            return 0;
        }

        billRepository.deleteAll(billsToDelete);

        log.info("{} factures supprimées avec succès", billsToDelete.size());
        return billsToDelete.size();
    }

    /**
     * Creates a BillRequest dynamically based on customer data and current billing
     * period
     *
     * @param customer    : customer whose bill request has to be generated
     * @param billingDate : the date the bill is generated
     * @return BillRequest
     */
    private BillRequest createBillRequestFromCustomer(Customers customer, LocalDateTime billingDate) {
        LocalDateTime dueDate = billingDate.plusDays(10);
        String deadLine = DATE_FORMAT.format(dueDate);

        // Calculer la dette antérieure (factures impayées des mois précédents)
        int currentMonth = billingDate.getMonth().getValue();
        int currentYear = billingDate.getYear();
        Bills previousBill = billRepository.findBillsByCustomersAndMonthAndYear(customer, currentMonth - 1, currentYear);
        BigDecimal previousDebt = previousBill.getNetToPay().subtract(previousBill.getPaidAmount());

        return BillRequest.builder()
                .monthlyPayment(DEFAULT_MONTHLY_AMOUNT)
                .deadline(deadLine)
                .month(currentMonth)
                .year(currentYear)
                .depositDate(DATE_FORMAT.format(billingDate))
                .paidAmount(BigDecimal.ZERO)
                .penalties(0)
                .observation("Vous serez suspendu si vous n'avez pas payé après la date limite de paiement.")
                .customerId(customer.getCustomerId())
                .debt(previousDebt) // Dette antérieure calculée
                .build();
    }

    /**
     * helper method to group the creation and the editing of a customer bill
     *
     * @param customer     : customer whose bill has to be created or updated
     * @param request      : the bill request for each customer
     * @param billingDate: the date when the bill is created or updated
     * @return the created or updated bill
     */
    private Bills createOrUpdateBill(Customers customer, BillRequest request, LocalDateTime billingDate) {
        int month = request.getMonth() != null ? request.getMonth() : billingDate.getMonth().getValue();
        int year = request.getYear() != null ? request.getYear() : billingDate.getYear();

        Bills existingBill = billRepository.findBillsByCustomersAndMonthAndYear(customer, month, year);
        BigDecimal netToPay = request.getDebt().add(request.getMonthlyPayment());

        if (existingBill != null) {
            billMapper.updateExistingBill(existingBill, request, netToPay, request.getDebt(), billingDate);
            return billRepository.save(existingBill);
        } else {
            Bills newBill = billMapper.createNewBill(customer, request, netToPay, request.getDebt(), billingDate, month, year);
            return billRepository.save(newBill);
        }
    }

    /**
     * Met à jour le statut de paiement d'une facture selon son solde
     */
    private void updateBillPaymentStatus(Bills bill) {
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
}
