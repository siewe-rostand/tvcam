package com.siewe_rostand.tvcam.Bills.services;

import com.siewe_rostand.tvcam.Bills.dto.BillMapper;
import com.siewe_rostand.tvcam.Bills.dto.BillRequest;
import com.siewe_rostand.tvcam.Bills.dto.BillResponse;
import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Bills.service.BillCalculationService;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.shared.Exceptions.OperationNotPermittedException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class BillServicesImpl implements BillServices {
    private static final Logger log = LoggerFactory.getLogger(BillServicesImpl.class);
    private final BillRepository billRepository;
    private final BillCalculationService billCalculationService;
    private final ObjectsValidator<BillRequest> validator;
    private final BillMapper billMapper;
    private final CustomersRepository customersRepository;

    public BillServicesImpl(BillRepository billRepository,
                            BillCalculationService billCalculationService,
                            ObjectsValidator<BillRequest> validator, BillMapper billMapper, CustomersRepository customersRepository) {
        this.billRepository = billRepository;
        this.billCalculationService = billCalculationService;
        this.validator = validator;
        this.billMapper = billMapper;
        this.customersRepository = customersRepository;
    }

    @Override
    public Bills update(BillRequest request) {
        return null;
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) {
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
    public HttpResponse<Object> findCustomerBills(Long customerId) {
        Customers customer = customersRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException(
                "No customer with ID " + customerId + " found!. Please Enter a Valid Customer ID"));

        List<Bills> bills = billRepository.findAllByCustomers(customer);
        List<BillResponse> billResponses = new ArrayList<>();
        for (Bills bill : bills) {
            BillResponse billResponse = billMapper.toResponse(bill);
            billResponses.add(billResponse);
        }

        return HttpResponse.builder().success(true).timestamp(now())
                .data(billResponses).status(OK.getReasonPhrase())
                .message("Bills for customer " + customer.getName() + " gotten successfully").statusCode(OK.value())
                .build();
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
    }

    private PaginatedResponse buildResponse(Page<Bills> bills, Pageable pageable) {
        Page<BillResponse> responses = bills.map(billMapper::toResponse);
        return PaginatedResponse.builder()
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
    public HttpResponse<Object> delete(Long id) {
        Bills bills = billRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucune facture avec cet identifiant " + id
                        + "n'a été trouvée ! Veuillez saisir un numéro de facture valide"));
        billRepository.delete(bills);
        return HttpResponse.builder().timestamp(now()).success(true)
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
        List<Customers> customers = customersRepository.findAllById(customerIds);

        List<BillResponse> generatedBills = new ArrayList<>();
        LocalDateTime today = now();

        for (Customers c : customers) {
            if (shouldGenerateBill(c, today) || shouldGenerate) {
                BillRequest dynamicRequest = createBillRequestFromCustomer(c, today);
                BillResponse response = generateBillForCustomer(c, today, dynamicRequest);
                generatedBills.add(response);
            }
        }
        return generatedBills;
    }

    @Transactional
    public BillResponse generateBillForCustomer(Customers customer, LocalDateTime billingDate, BillRequest request) {
        log.info("bill request: {}", request.toString());
        validator.validate(request);

        Bills savedBill = createOrUpdateBill(customer, request, billingDate);
        System.out.println("savedBill =************** " + savedBill);
        customer.setLastBillGenerationDate(billingDate);
        customersRepository.save(customer);
        return billMapper.toResponse(savedBill);
    }

    /**
     * Détermine si une nouvelle facture doit être générée pour un client.
     *
     * @param customer L'objet Customer contenant les informations sur le client.
     * @param today    La date et l'heure actuelles.
     * @return Vrai si une facture doit être générée, sinon faux.
     */
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

        List<Customers> customers = customersRepository.findAllById(customerIds);

        for (Customers customer : customers) {
            List<Bills> existingBills = billRepository.findByCustomersAndMonthAndYear(customer, month, year);
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

        return BillRequest.builder()
                .monthlyPayment(DEFAULT_MONTHLY_AMOUNT)
                .deadline(deadLine)
                .month(billingDate.getMonth().getValue())
                .year(billingDate.getYear())
                .depositDate(DATE_FORMAT.format(billingDate))
                .paidAmount(BigDecimal.ZERO)
                .penalties(0)
                .observation("Vous serez suspendu si vous n'avez pas payé après la date limite de paiement.")
                .customerId(customer.getCustomerId())
                .debt(BigDecimal.ZERO)
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
        BigDecimal netToPay = billCalculationService.calculateBillAmount(customer);
        BigDecimal debt = billCalculationService.getUnpaidAmount(customer);

        int month = request.getMonth() != null ? request.getMonth() : billingDate.getMonth().getValue();
        int year = request.getYear() != null ? request.getYear() : billingDate.getYear();

        Bills existingBill = billRepository.findBillsByCustomersAndMonthAndYear(customer, month, year);

        if (existingBill != null) {
            billMapper.updateExistingBill(existingBill, request, netToPay, debt, billingDate);
            return billRepository.save(existingBill);
        } else {
            Bills newBill = billMapper.createNewBill(customer, request, netToPay, debt, billingDate, month, year);
            return billRepository.save(newBill);
        }
    }
}
