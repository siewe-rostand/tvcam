package com.siewe_rostand.tvcam.Payment;

import org.springframework.stereotype.Component;

/**
 * @author rostand
 * @project tv-cam
 */

@Component
public class PaymentMapper {


    public Payments toPayment(PaymentRequest request) {
        return Payments.builder()
                .amount(request.getAmount())
                .observation(request.getObservation())
                .build();
    }

    public PaymentResponse toResponse(Payments payments) {
        return PaymentResponse.builder()
                .id(payments.getPaymentId())
                .paymentMethod(payments.getPaymentMethod().name())
                .paymentAmount(payments.getAmount())
                .paymentDate(payments.getPaymentDate())
                .paymentStatus(payments.getBills().getPaymentStatus().name())
                .paymentReference(payments.getPaymentRef())
                .customerName(payments.bills.getCustomers().getName())
                .customerId(payments.bills.getCustomers().getCustomerId())
                .month(payments.bills.getMonth())
                .remainingBalance(payments.bills.getNetToPay().subtract(payments.bills.getPaidAmount()))
                .totalPaid(payments.bills.getPaidAmount())
                .customerPaymentFrequency(payments.bills.getCustomers().getPaymentFrequency().name())
//                .user(payments.users.getFullName()) //TODO: set the user who created the payment
                .build();
    }
}
