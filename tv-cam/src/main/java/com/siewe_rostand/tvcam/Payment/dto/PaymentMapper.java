package com.siewe_rostand.tvcam.Payment.dto;

import com.siewe_rostand.tvcam.Payment.model.Payments;
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
        .customerName(payments.getBills().getCustomers().getName())
        .customerId(payments.getBills().getCustomers().getCustomerId())
        .month(payments.getBills().getMonth())
        .remainingBalance(
            payments.getBills().getNetToPay().subtract(payments.getBills().getPaidAmount()))
        .totalPaid(payments.getBills().getPaidAmount())
        .customerPaymentFrequency(payments.getBills().getCustomers().getPaymentFrequency().name())
        //                .user(payments.users.getFullName()) //TODO: set the user who created the
        // payment
        .build();
    }
}
