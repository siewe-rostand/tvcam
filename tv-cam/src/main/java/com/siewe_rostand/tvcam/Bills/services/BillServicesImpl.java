package com.siewe_rostand.tvcam.Bills.services;

import com.siewe_rostand.tvcam.Bills.dto.BillMapper;
import com.siewe_rostand.tvcam.Bills.dto.BillRequest;
import com.siewe_rostand.tvcam.Bills.dto.BillResponse;
import com.siewe_rostand.tvcam.Bills.dto.BillSDto;
import com.siewe_rostand.tvcam.Bills.model.Bills;
import com.siewe_rostand.tvcam.Bills.repository.BillRepository;
import com.siewe_rostand.tvcam.Bills.service.BillCalculationService;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Customers.repository.CustomersRepository;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.common.exceptions.ApiException;
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

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.FORMATTER;
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

    @Transactional
    @Override
    public BillResponse save(BillRequest request) {
        validator.validate(request);
        // customerService.checkIfCustomerExistsOrThrow(request.getCustomerId());
        Bills bills = billMapper.toBills(request);
        Bills savedBills = billRepository.save(bills);
        return billMapper.toResponse(savedBills);
    }

    @Override
    public Bills update(BillSDto billSDto) {
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
    public BillResponse generateBills(BillRequest request) {
        LocalDateTime today = now();
        BillResponse response = new BillResponse();
        List<Customers> customers = customersRepository.findAll();
        for (Customers c : customers) {
            try {
                if (shouldGenerateBill(c, today)) {
                    // Use provided request or create dynamic one if request is empty/null
                    BillRequest billRequest = (request != null && hasValidData(request))
                            ? request
                            : createBillRequestFromCustomer(c, today);
                    response = generateBillForCustomer(c, today, billRequest);
                }
            } catch (Exception e) {
                log.error("Error generating bill for customer {}{}", c.getCustomerId(), e.getMessage());
            }
        }

        return response;
    }

    @Transactional
    @Override
    public List<BillResponse> generateBillsForSelectedCustomers(List<Long> customerIds, Boolean shouldGenerate) {
        log.debug("bill request: {}", customerIds);
        List<Customers> customers = customersRepository.findAllById(customerIds);

        List<BillResponse> generatedBills = new ArrayList<>();
        LocalDateTime today = now();

        for (Customers c : customers) {
            try {
                if (shouldGenerateBill(c, today) || shouldGenerate) {
                    BillRequest dynamicRequest = createBillRequestFromCustomer(c, today);
                    BillResponse response = generateBillForCustomer(c, today, dynamicRequest);
                    generatedBills.add(response);
                }
            } catch (Exception e) {
                log.trace("Error generating bill for customer {}", e.getMessage());
                throw new ApiException("Error generating bill for customer\n" + c.getCustomerId() + " " + e);
            }
        }
        return generatedBills;
    }

    @Transactional
    public BillResponse generateBillForCustomer(Customers customer, LocalDateTime billingDate, BillRequest request) {
        validator.validate(request);

        BigDecimal netToPay = calculateBillAmount(customer);
        BigDecimal debt = getUnpaidAmount(customer);
        LocalDateTime dueDate = billingDate.plusDays(10);
        String deadLine = FORMATTER.format(dueDate);

        Bills newBill = Bills.builder()
                .customers(customer)
                .monthlyPayment(
                        request.getMonthlyPayment() == null ? BigDecimal.valueOf(2000) : request.getMonthlyPayment())
                .deadline((request.getDeadline() == null || request.getDeadline().isEmpty()) ? deadLine
                        : request.getDeadline())
                .paymentStatus(PaymentStatus.UNPAID)
                .debt(debt)
                .observation(request.getObservation())
                .month((request.getMonth() == null || request.getMonth().isEmpty())
                        ? billingDate.getMonth().name().toLowerCase()
                        : request.getMonth())
                .year((request.getYear() == null || request.getYear().isEmpty()) ? String.valueOf(billingDate.getYear())
                        : request.getYear())
                .depositDate(
                        request.getDepositDate() != null ? request.getDepositDate() : FORMATTER.format(billingDate))
                .penalties(request.getPenalties())
                .currentPeriodBill(true)
                .paidAmount(request.getPaidAmount() == null ? BigDecimal.ZERO : request.getPaidAmount())
                .netToPay(netToPay)
                .build();

        Bills savedBill = billRepository.save(newBill);
        customer.setLastBillGenerationDate(billingDate);
        customersRepository.save(customer);
        return billMapper.toResponse(savedBill);
    }

    private boolean shouldGenerateBill(Customers customer, LocalDateTime today) {

        // Check if the last bill generation date is null or if the current date is the
        // first of the month
        boolean shouldGenerate = customer.getLastBillGenerationDate() == null ||
                today.getDayOfMonth() == 1;

        if (shouldGenerate) {
            return true;
        }

        long monthsSinceLastBill = ChronoUnit.MONTHS.between(customer.getLastBillGenerationDate(), today);

        // Handle null payment frequency by using default (MONTHLY)
        PaymentFrequency frequency = customer.getPaymentFrequency();
        if (frequency == null) {
            log.warn("Customer {} has null payment frequency, using default MONTHLY", customer.getCustomerId());
            frequency = PaymentFrequency.DEFAULT;

            // Update the customer with the default frequency to prevent future issues
            customer.setPaymentFrequency(frequency);
            customersRepository.save(customer);
        }

        return switch (frequency) {
            case MONTHLY -> monthsSinceLastBill >= 1;
            case QUARTERLY -> monthsSinceLastBill >= 3;
            case SEMI_ANNUALLY -> monthsSinceLastBill >= 6;
            case ANNUALLY -> monthsSinceLastBill >= 12;
        };
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateBillAmount(Customers customer) {
        // Utiliser BillCalculationService pour les calculs avec rabais
        BigDecimal debt = getUnpaidAmount(customer);
        return billCalculationService.calculateBillAmount(customer, debt);
    }

    @Transactional(readOnly = true)
    public BigDecimal getUnpaidAmount(Customers customer) {
        List<Bills> unpaidBills = billRepository.findAllByCustomersAndPaymentStatus(customer, PaymentStatus.UNPAID);
        return unpaidBills.stream()
                .map(Bills::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
    public boolean checkExistingBillsForMonth(List<Long> customerIds, String month, String year) {
        log.info("Vérification de l'existence de factures pour {} clients, mois: {}, année: {}",
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
     */
    private BillRequest createBillRequestFromCustomer(Customers customer, LocalDateTime billingDate) {
        LocalDateTime dueDate = billingDate.plusDays(10);
        String deadLine = FORMATTER.format(dueDate);

        return BillRequest.builder()
                .monthlyPayment(BigDecimal.valueOf(2000)) // Default monthly payment
                .deadline(deadLine)
                .month(billingDate.getMonth().name().toLowerCase())
                .year(String.valueOf(billingDate.getYear()))
                .depositDate(FORMATTER.format(billingDate))
                .paidAmount(BigDecimal.ZERO)
                .penalties(0) // Using Integer as per BillRequest definition
                .observation("Bill generated automatically for " + customer.getName())
                .customerId(customer.getCustomerId())
                .build();
    }

    /**
     * Checks if a BillRequest has valid/meaningful data
     */
    private boolean hasValidData(BillRequest request) {
        return request.getMonthlyPayment() != null ||
                (request.getMonth() != null && !request.getMonth().isEmpty()) ||
                (request.getYear() != null && !request.getYear().isEmpty()) ||
                (request.getDeadline() != null && !request.getDeadline().isEmpty()) ||
                request.getPaidAmount() != null ||
                request.getPenalties() != null ||
                (request.getObservation() != null && !request.getObservation().isEmpty()) ||
                request.getCustomerId() != null;
    }
}
