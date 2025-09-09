package com.siewe_rostand.tvcam.Customers.dto;


import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import org.springframework.stereotype.Component;

import static com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency.MONTHLY;
import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.FORMATTER;

@Component
public class CustomerMapper {
    public CustomerResponse toResponse(Customers customers) {
        return CustomerResponse.builder().id(customers.getCustomerId()).name(customers.getName()).ref(customers.getRef())
                .address(customers.getAddress()).telephone(customers.getTelephone()).hasDebt(customers.getHasDebt()).hasPaid(customers.getHasPaid())
                .isActive(customers.getIsActive()).isSuspended(customers.getIsSuspended())
                .paymentFrequency(customers.getPaymentFrequency() == null ? MONTHLY.name() : customers.getPaymentFrequency().name())
                .lastBillGenerationDate(customers.getLastBillGenerationDate() != null ? FORMATTER.format(customers.getLastBillGenerationDate()) : null)
                .build();
    }

    public Customers toEntity(CustomerRequest request) {
        return Customers.builder().name(request.getName()).address(request.getAddress())
                .hasPaid(request.getHasPaid()).hasDebt(request.getHasDebt()).telephone(request.getTelephone()).isActive(request.getIsActive())
                .paymentFrequency(request.getPaymentFrequency() == null ? MONTHLY : PaymentFrequency.valueOf(request.getPaymentFrequency()))
                .isSuspended(request.getIsSuspended()).build();
    }
}
