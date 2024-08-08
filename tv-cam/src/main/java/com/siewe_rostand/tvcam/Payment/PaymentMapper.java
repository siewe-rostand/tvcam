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
                .build();
    }
}
