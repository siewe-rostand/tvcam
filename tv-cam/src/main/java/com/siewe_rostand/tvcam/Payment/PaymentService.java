package com.siewe_rostand.tvcam.Payment;


import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse save(PaymentRequest paymentRequest);

    PaginatedResponse findAll(Integer page, Integer size, String sortBy, String direction, String name);
    List<PaymentResponse> findPaymentByCustomerId(Long customerId);

    List<PaymentResponse> findByBills_Month(String month);
}
