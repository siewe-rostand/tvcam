package com.siewe_rostand.tvcam.Payment.services;


import com.siewe_rostand.tvcam.Payment.dto.PaymentRequest;
import com.siewe_rostand.tvcam.Payment.dto.PaymentResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest paymentRequest);

    PaginatedResponse<PaymentResponse> findAll(Integer page, Integer size, String sortBy, String direction, String name);

    List<PaymentResponse> findPaymentByCustomerId(Long customerId);

    List<PaymentResponse> findByBills_Month(Integer month);
}
