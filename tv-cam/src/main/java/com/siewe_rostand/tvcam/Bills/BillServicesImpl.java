package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Customers.CustomersRepository;
import com.siewe_rostand.tvcam.Payment.PaymentStatus;
import com.siewe_rostand.tvcam.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import com.siewe_rostand.tvcam.validator.ObjectsValidator;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class BillServicesImpl implements BillServices {
    private static final Logger log = LoggerFactory.getLogger(BillServicesImpl.class);
    private final BillRepository billRepository;

    private final ObjectsValidator<BillRequest> validator;
    private final BillMapper billMapper;
    private final CustomersRepository customersRepository;

    public BillServicesImpl(BillRepository billRepository,
                            ObjectsValidator<BillRequest> validator, BillMapper billMapper, CustomersRepository customersRepository) {
        this.billRepository = billRepository;
        this.validator = validator;
        this.billMapper = billMapper;
        this.customersRepository = customersRepository;
    }

    @Transactional
    @Override
    public BillResponse save(BillRequest request) {
        validator.validate(request);
//        customerService.checkIfCustomerExistsOrThrow(request.getCustomerId());
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
    public HttpResponse findCustomerBills(Long customerId) {
        Customers customer = customersRepository.findById(customerId).orElseThrow(()
                -> new EntityNotFoundException("No customer with ID " + customerId + " found!. Please Enter a Valid Customer ID"));

        List<Bills> bills = billRepository.findAllByCustomers(customer);
        List<BillResponse> billResponses = new ArrayList<>();
        for (Bills bill : bills) {
            BillResponse billResponse = billMapper.toResponse(bill);
            billResponses.add(billResponse);
        }

        return HttpResponse.builder().content(billResponses).status(OK).message("Bills for customer " + customer.getName() + " gotten successfully").statusCode(OK.value()).build();
    }


    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
    }

    private PaginatedResponse buildResponse(Page<Bills> bills, Pageable pageable) {
        Page<BillResponse> responses = bills.map(billMapper::toResponse);
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
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
    public BillSDto findById(Long id) {
        return null;
    }

    @Override
    public HttpResponse delete(Long id) {
        Bills bills = billRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Aucune facture avec cet identifiant " + id + "n'a été trouvée ! Veuillez saisir un numéro de facture valide"));
        billRepository.delete(bills);
        return HttpResponse.builder().message("Facture supprimée avec succès").statusCode(OK.value()).status(OK).build();
    }

    @Transactional
    @Override
    public BillResponse generateBills(BillRequest request) {
        LocalDateTime today = LocalDateTime.now();
        BillResponse response = new BillResponse();
        List<Customers> customers = customersRepository.findAll();
        for (Customers c : customers) {
            try {
                if (shouldGenerateBill(c, today))
                    response = generateBillForCustomer(c, today, request);
            } catch (Exception e) {
                log.error("Error generating bill for customer {}{}", c.getCustomerId(), e.getMessage());
            }
        }

        return response;
    }

    @Transactional
    @Override
    public List<BillResponse> generateBillsForSelectedCustomers(List<Long> customerIds) {
        log.debug("bill request: {}", customerIds);
        List<BillResponse> generatedBills = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();

        List<Customers> customers = customersRepository.findAllById(customerIds);

        for (Customers c : customers) {
            try {
                if (shouldGenerateBill(c, today)) {
                    BillResponse response = generateBillForCustomer(c, today, new BillRequest());
                    generatedBills.add(response);
                }
            } catch (Exception e) {
                log.trace("Error generating bill for customer {}", e.getMessage());
                throw new ApiException("Error generating bill for customer" + c.getCustomerId() + " " + e);
                // Optionally, you could throw a custom exception here to be handled by the controller
            }
        }
        return generatedBills;
    }

    @Transactional
    public BillResponse     generateBillForCustomer(Customers customer, LocalDateTime billingDate, BillRequest request) {
        validator.validate(request);

        BigDecimal netToPay = calculateBillAmount(customer);
        BigDecimal debt = getUnpaidAmount(customer);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        LocalDateTime dueDate = billingDate.plusDays(10);
        String deadLine = formatter.format(dueDate);

        Bills newBill = Bills.builder()
                .customers(customer)
                .monthlyPayment(request.getMonthlyPayment() == null ? BigDecimal.valueOf(2000) : request.getMonthlyPayment())
                .deadline((request.getDeadline() == null || request.getDeadline().isEmpty()) ? deadLine : request.getDeadline())
                .paymentStatus(PaymentStatus.UNPAID)
                .debt(debt)
                .observation(request.getObservation())
                .month((request.getMonth() == null || request.getMonth().isEmpty()) ? billingDate.getMonth().name().toLowerCase() : request.getMonth())
                .year((request.getYear() == null || request.getYear().isEmpty()) ? String.valueOf(billingDate.getYear()) : request.getYear())
                .depositDate(request.getDepositDate())
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

        if (customer.getLastBillGenerationDate() == null) {
            return true;
        }

        long monthsSinceLastBill = ChronoUnit.MONTHS.between(customer.getLastBillGenerationDate(), today);

        return switch (customer.getPaymentFrequency()) {
            case MONTHLY -> monthsSinceLastBill >= 1;
            case QUARTERLY -> monthsSinceLastBill >= 3;
            case SEMI_ANNUALLY -> monthsSinceLastBill >= 6;
            case ANNUALLY -> monthsSinceLastBill >= 12;
        };
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateBillAmount(Customers customer) {
        BigDecimal baseAmount = new BigDecimal("2000.00"); // Example base amount
        BigDecimal unpaidAmount = getUnpaidAmount(customer);
        return baseAmount.add(unpaidAmount);
    }

    @Transactional(readOnly = true)
    public BigDecimal getUnpaidAmount(Customers customer) {
        List<Bills> unpaidBills = billRepository.findAllByCustomersAndPaymentStatus(customer, PaymentStatus.UNPAID);
        return unpaidBills.stream()
                .map(Bills::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
