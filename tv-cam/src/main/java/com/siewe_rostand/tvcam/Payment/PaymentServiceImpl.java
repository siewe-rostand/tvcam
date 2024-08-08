package com.siewe_rostand.tvcam.Payment;

import com.siewe_rostand.tvcam.Bills.BillRepository;
import com.siewe_rostand.tvcam.Bills.Bills;
import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Customers.CustomersRepository;
import com.siewe_rostand.tvcam.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import com.siewe_rostand.tvcam.validator.ObjectsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentReferenceGenerator paymentReferenceGenerator;
    private final BillRepository billRepository;
    private final CustomersRepository customersRepository;
    private final ObjectsValidator<PaymentRequest> validator;


    @Override
    public PaymentResponse save(PaymentRequest paymentRequest) {
        validator.validate(paymentRequest);
        Customers customers = customersRepository.findById(paymentRequest.customerId)
                .orElseThrow(() -> new EntityNotFoundException(Customers.class, "customerId" + paymentRequest.customerId.toString()));

        Bills currentBills = billRepository.findByCustomersAndCurrentPeriodBill(customers, true)
                .orElseThrow(() -> new EntityNotFoundException(Bills.class, "bill id" + paymentRequest.billId));

        if (currentBills.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ApiException("Trying to pay a bill which have already been paid", "Current bill has already been paid");
        }
        if(paymentRequest.customerPaymentFrequency != null){
            customers.setPaymentFrequency(PaymentFrequency.valueOf(paymentRequest.getCustomerPaymentFrequency()));
            customersRepository.save(customers);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.FRANCE);
        LocalDate now = LocalDate.now();

        Payments payments = new Payments();
        payments.setBills(currentBills);
        payments.setPaymentDate(formatter.format(now));

        payments.setPaymentRef(paymentReferenceGenerator.generatePaymentReference());
        payments.setAmount(paymentRequest.getAmount());
        payments.setPaymentMethod(PaymentMethod.valueOf(paymentRequest.getPaymentMethod()));
        payments.setObservation(paymentRequest.getObservation());
        paymentRepository.save(payments);

        if (payments.getAmount().compareTo(currentBills.getNetToPay()) >= 0) {
            currentBills.setPaymentStatus(PaymentStatus.PAID);
            currentBills.setNetToPay(BigDecimal.valueOf(0));
        } else {
            BigDecimal rest = currentBills.getNetToPay().subtract(payments.getAmount());
            currentBills.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
            currentBills.setNetToPay(rest);
        }

        billRepository.save(currentBills);
        return paymentMapper.toResponse(payments);
    }

    @Override
    public PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name) {
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
        List<Payments> payments = paymentRepository.findByBills_CustomersCustomerId(customerId);
        List<PaymentResponse> paymentResponses = new ArrayList<>();
        for (Payments payment : payments) {
            paymentResponses.add(paymentMapper.toResponse(payment));
        }
        return paymentResponses;
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        return PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
    }

    private PaginatedResponse buildResponse(Page<Payments> payments, Pageable pageable) {
        Page<PaymentResponse> responses = payments.map(paymentMapper::toResponse);
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK).statusCode(HttpStatus.OK.value())
                .message("Payments gotten successfully")
                .data(responses.getContent())
                .lastPage(responses.isLast()).firstPage(responses.isFirst())
                .totalPages(responses.getTotalPages()).totalElements(responses.getNumberOfElements())
                .empty(responses.isEmpty()).sorted(responses.getSort().isSorted())
                .numberOfElements(responses.getNumberOfElements())
                .paged(pageable.isPaged()).page(pageable.getPageNumber())
                .build();
    }
}
